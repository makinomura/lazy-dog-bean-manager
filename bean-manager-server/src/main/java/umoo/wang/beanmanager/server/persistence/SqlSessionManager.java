package umoo.wang.beanmanager.server.persistence;

import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import umoo.wang.beanmanager.common.exception.ServerException;
import umoo.wang.beanmanager.server.persistence.support.DynamicMapperCreator;
import umoo.wang.beanmanager.server.persistence.support.InsertKeyInterceptor;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created by yuanchen on 2019/01/20.
 */
public class SqlSessionManager {
	private final static String DEFAULT_ENVIRONMENT = "default";
	private final static Logger logger = LoggerFactory
			.getLogger(SqlSessionManager.class);
	private static SqlSessionFactory sqlSessionFactory;
	private static DynamicMapperCreator mapperCreator = new DynamicMapperCreator();

	static {
		buildSqlSessionFactory();
	}

	public static DelegateSqlSession openSession() {
		return openSession(true, false);
	}

	public static DelegateSqlSession openSession(boolean autoCommit,
			boolean readonly) {
		SqlSession sqlSession = sqlSessionFactory.openSession(autoCommit);
		return readonly ? new ReadonlySqlSession(sqlSession, mapperCreator)
				: new DelegateSqlSession(sqlSession, mapperCreator);
	}

	public static void execute(Consumer<DelegateSqlSession> consumer) {
		execute(false, consumer);
	}

	public static void execute(boolean readonly,
			Consumer<DelegateSqlSession> consumer) {
		execute(readonly, sqlSession -> {
			consumer.accept(sqlSession);
			return null;
		});
	}

	public static <T> T execute(Function<DelegateSqlSession, T> function) {
		return execute(false, function);
	}

	public static <T> T execute(boolean readonly,
			Function<DelegateSqlSession, T> function) {
		DelegateSqlSession sqlSession = null;
		Exception e = null;

		try {
			sqlSession = openSession(false, readonly);
			T result = function.apply(sqlSession);

			sqlSession.commit();
			return result;
		} catch (Exception ex) {
			e = ex;

			if (sqlSession != null) {
				sqlSession.rollback();
			}
		} finally {
			if (sqlSession != null) {
				sqlSession.close();
			}
		}

		throw ServerException.wrap(e);
	}

	private static void buildSqlSessionFactory() {

		JdbcConfig config = JdbcConfig.read();

		try {
			Class.forName(config.getDriver());

			SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();

			Environment environment = new Environment(DEFAULT_ENVIRONMENT,
					new JdbcTransactionFactory(),
					new PooledDataSource(config.getDriver(), config.getUrl(),
							config.getUsername(), config.getPassword()));

			Configuration configuration = new Configuration(environment);
			configuration.setLazyLoadingEnabled(true);
			configuration.setMapUnderscoreToCamelCase(true);
			configuration.addInterceptor(new InsertKeyInterceptor());
			configuration.setUseGeneratedKeys(true);

			sqlSessionFactory = sqlSessionFactoryBuilder.build(configuration);
		} catch (ClassNotFoundException e) {
			logger.error("Connection establish failed!", e);
			throw ServerException.wrap(e);
		}
	}
}
