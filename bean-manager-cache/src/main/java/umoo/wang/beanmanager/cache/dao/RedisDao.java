package umoo.wang.beanmanager.cache.dao;

import umoo.wang.beanmanager.cache.entity.ClientInfo;

import java.util.List;

/**
 * Created by yuanchen on 2019/02/22.
 */
public interface RedisDao {
	List<ClientInfo> listClientInfo();

	Long addClientInfo(ClientInfo clientInfo);

	Long removeClientInfo(String key);
}
