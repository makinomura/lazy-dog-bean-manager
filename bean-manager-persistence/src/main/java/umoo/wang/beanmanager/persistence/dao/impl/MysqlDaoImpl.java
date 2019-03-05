package umoo.wang.beanmanager.persistence.dao.impl;

import umoo.wang.beanmanager.common.beanfactory.Bean;
import umoo.wang.beanmanager.common.beanfactory.Inject;
import umoo.wang.beanmanager.persistence.SqlSessionExecutor;
import umoo.wang.beanmanager.persistence.dao.MysqlDao;
import umoo.wang.beanmanager.persistence.entity.App;
import umoo.wang.beanmanager.persistence.support.Mapper;

import java.util.List;

/**
 * Created by yuanchen on 2019/02/25.
 */
@Bean
public class MysqlDaoImpl implements MysqlDao {

	@Inject
	private SqlSessionExecutor sqlSessionExecutor;

	@Override
	public List<App> listApp() {
		return sqlSessionExecutor.execute(true, delegateSqlSession -> {
			Mapper<Integer, App> appMapper = delegateSqlSession
					.getMapperWithEntityClazz(App.class);

			return appMapper.listAll();
		});
	}
}
