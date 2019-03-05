package umoo.wang.beanmanager.persistence.dao;

import umoo.wang.beanmanager.persistence.entity.App;

import java.util.List;

/**
 * Created by yuanchen on 2019/02/25.
 */
public interface MysqlDao {
	List<App> listApp();
}
