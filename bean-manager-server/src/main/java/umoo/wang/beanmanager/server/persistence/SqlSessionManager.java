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
import umoo.wang.beanmanager.server.persistence.support.InsertKeyInterceptor;
import umoo.wang.beanmanager.server.persistence.transaction.DelegateTransactionFactory;

import java.util.List;

/**
 * Created by yuanchen on 2019/01/20.
 */
public class SqlSessionManager {
	private final static String DEFAULT_ENVIRONMENT = "default";
	private final static Logger logger = LoggerFactory
			.getLogger(SqlSessionManager.class);
	private static ThreadLocal<SqlSession> currentSqlSession = new ThreadLocal<>();
	private static SqlSessionFactory delegate;
	private List<Class<?>> mapperInterfaces;

	public SqlSessionManager(List<Class<?>> mapperInterfaces) {
		this.mapperInterfaces = mapperInterfaces;
		buildDelegate();
	}

	public static SqlSession getCurrentSqlSession() {
		return currentSqlSession.get();
	}

	public static <T> T getMapper(Class<T> clazz) {
		if (currentSqlSession.get() == null) {
			openSession(true);
		}

		return currentSqlSession.get().getMapper(clazz);
	}

	public static SqlSession openSession() {
		return openSession(true);
	}

	public static SqlSession openSession(boolean autoCommit) {
		SqlSession sqlSession = delegate.openSession(autoCommit);
		currentSqlSession.set(sqlSession);
		return sqlSession;
	}

	private void buildDelegate() {
		if (delegate != null) {
			logger.warn("SqlSessionManager is already built.");
		}

		JdbcConfig config = JdbcConfig.read();

		try {
			Class.forName(config.getDriver());

			SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();

			Environment environment = new Environment(DEFAULT_ENVIRONMENT,
					new DelegateTransactionFactory(
							new JdbcTransactionFactory()),
					new PooledDataSource(config.getDriver(), config.getUrl(),
							config.getUsername(), config.getPassword()));

			Configuration configuration = new Configuration(environment);
			configuration.setLazyLoadingEnabled(true);
			configuration.setMapUnderscoreToCamelCase(true);
			configuration.addInterceptor(new InsertKeyInterceptor());
			configuration.setUseGeneratedKeys(true);

			mapperInterfaces.forEach(configuration::addMapper);

			delegate = sqlSessionFactoryBuilder.build(configuration);

		} catch (ClassNotFoundException e) {
			logger.error("Connection establish failed!", e);
			throw ServerException.wrap(e);
		}
	}
}
