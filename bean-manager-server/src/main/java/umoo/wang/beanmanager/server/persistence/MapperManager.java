package umoo.wang.beanmanager.server.persistence;

import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import umoo.wang.beanmanager.common.exception.ServerException;
import umoo.wang.beanmanager.server.persistence.transaction.DelegateTransactionFactory;

import java.util.List;

/**
 * Created by yuanchen on 2019/01/20.
 */
public class MapperManager {
	private final static String DEFAULT_ENVIRONMENT = "default";
	private final static Logger logger = LoggerFactory
			.getLogger(MapperManager.class);
	private SqlSessionManager sqlSessionManager;
	private List<Class<?>> mapperInterfaces;

	public MapperManager(List<Class<?>> mapperInterfaces) {
		this.mapperInterfaces = mapperInterfaces;
		init();
	}

	public SqlSessionManager getSqlSessionManager() {
		return sqlSessionManager;
	}

	private void init() {
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

			mapperInterfaces.forEach(configuration::addMapper);

			SqlSessionFactory sqlSessionFactory = sqlSessionFactoryBuilder
					.build(configuration);
			sqlSessionManager = SqlSessionManager
					.newInstance(sqlSessionFactory);

		} catch (ClassNotFoundException e) {
			logger.error("Connection establish failed!", e);
			throw ServerException.wrap(e);
		}
	}

	public <T> T getMapper(Class<T> clazz) {
		return sqlSessionManager.openSession(ExecutorType.SIMPLE, true)
				.getMapper(clazz);
	}
}
