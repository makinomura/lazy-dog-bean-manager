package umoo.wang.beanmanager.cache.dao.impl;

import com.alibaba.fastjson.JSON;
import umoo.wang.beanmanager.cache.JedisExecutor;
import umoo.wang.beanmanager.cache.RedisKey;
import umoo.wang.beanmanager.cache.dao.RedisDao;
import umoo.wang.beanmanager.cache.entity.ClientInfo;
import umoo.wang.beanmanager.common.beanfactory.Bean;
import umoo.wang.beanmanager.common.beanfactory.Inject;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by yuanchen on 2019/02/22.
 */
@Bean
public class RedisDaoImpl implements RedisDao {

	@Inject
	private JedisExecutor jedisExecutor;

	@Override
	public List<ClientInfo> listClientInfo() {
		return jedisExecutor.execute(jedis -> {
			return jedis.hgetAll(RedisKey.CLIENT_INFO_MAP).values().stream()
					.map(bytes -> JSON.parseObject(bytes, ClientInfo.class))
					.collect(Collectors.toList());
		});
	}

	@Override
	public Long addClientInfo(ClientInfo clientInfo) {
		return jedisExecutor.execute(jedis -> {
			return jedis.hset(RedisKey.CLIENT_INFO_MAP,
					clientInfo.getChannelKey(), JSON.toJSONString(clientInfo));
		});
	}

	@Override
	public Long removeClientInfo(String key) {
		return jedisExecutor.execute(jedis -> {
			return jedis.hdel(RedisKey.CLIENT_INFO_MAP, key);
		});
	}
}
