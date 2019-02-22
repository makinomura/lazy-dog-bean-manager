package umoo.wang.beanmanager.web.controller;

import io.netty.handler.codec.http.FullHttpRequest;
import umoo.wang.beanmanager.cache.dao.RedisDao;
import umoo.wang.beanmanager.cache.entity.ClientInfo;
import umoo.wang.beanmanager.common.beanfactory.Bean;
import umoo.wang.beanmanager.common.beanfactory.Inject;
import umoo.wang.beanmanager.web.support.Mapping;

import java.util.List;

/**
 * Created by yuanchen on 2019/02/22.
 */
@Bean
@Mapping(path = "/client")
public class ClientController {

	@Inject
	private RedisDao redisDao;

	@Mapping(path = "/all")
	public List<ClientInfo> all(FullHttpRequest request) {
		return redisDao.listClientInfo();
	}
}
