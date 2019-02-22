package umoo.wang.beanmanager.persistence;

import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import umoo.wang.beanmanager.common.ResourceExecutor;
import umoo.wang.beanmanager.common.beanfactory.Bean;
import umoo.wang.beanmanager.common.beanfactory.Inject;
import umoo.wang.beanmanager.common.exception.ManagerException;
import umoo.wang.beanmanager.persistence.support.DynamicMapperCreator;
import umoo.wang.beanmanager.persistence.support.InsertKeyInterceptor;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created by yuanchen on 2019/01/20.
 */
@Bean
public class SqlSessionExecutor
		implements ResourceExecutor<DelegateSqlSession> {
	private final static String DEFAULT_ENVIRONMENT = "default";
	private final static Logger logger = LoggerFactory
			.getLogger(SqlSessionExecutor.class);

	@Inject
	private SqlSessionFactory sqlSessionFactory;

	@Inject
	private DynamicMapperCreator mapperCreator;

	@Inject
	private JdbcConfig jdbcConfig;

	public DelegateSqlSession openSession() {
		return openSession(true, false);
	}

	public DelegateSqlSession openSession(boolean autoCommit,
			boolean readonly) {
		SqlSession sqlSession = sqlSessionFactory.openSession(autoCommit);
		return readonly ? new ReadonlySqlSession(sqlSession, mapperCreator)
				: new DelegateSqlSession(sqlSession, mapperCreator);
	}

	@Override
	public DelegateSqlSession getResource() {
		return openSession();
	}

	@Override
	public void execute(Consumer<DelegateSqlSession> consumer) {
		execute(false, consumer);
	}

	public void execute(boolean readonly,
			Consumer<DelegateSqlSession> consumer) {
		execute(readonly, sqlSession -> {
			consumer.accept(sqlSession);
			return null;
		});
	}

	@Override
	public <T> T execute(Function<DelegateSqlSession, T> function) {
		return execute(false, function);
	}

	public <T> T execute(boolean readonly,
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

		throw ManagerException.wrap(e);
	}

	@Bean
	public SqlSessionFactory sqlSessionFactory(@Inject JdbcConfig jdbcConfig) {
		try {
			Class.forName(jdbcConfig.getDriver());

			SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();

			PooledDataSource dataSource = new PooledDataSource(
					jdbcConfig.getDriver(), jdbcConfig.getUrl(),
					jdbcConfig.getUsername(), jdbcConfig.getPassword());

			// 连接保活
			dataSource.setPoolPingEnabled(true);
			dataSource.setPoolPingQuery("SELECT 1");
			dataSource.setPoolPingConnectionsNotUsedFor(60 * 60 * 1000);

			Environment environment = new Environment(DEFAULT_ENVIRONMENT,
					new JdbcTransactionFactory(), dataSource);

			Configuration configuration = new Configuration(environment);
			configuration.setLazyLoadingEnabled(true);
			configuration.setMapUnderscoreToCamelCase(true);
			configuration.addInterceptor(new InsertKeyInterceptor());
			configuration.setUseGeneratedKeys(true);

			return sqlSessionFactoryBuilder.build(configuration);
		} catch (ClassNotFoundException e) {
			logger.error("Connection establish failed!", e);
			throw ManagerException.wrap(e);
		}
	}

	@Bean
	public DynamicMapperCreator dynamicMapperCreator() {
		return new DynamicMapperCreator();
	}
}
