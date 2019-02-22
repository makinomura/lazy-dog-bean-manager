package umoo.wang.beanmanager.cache;

import redis.clients.jedis.Jedis;
import umoo.wang.beanmanager.common.beanfactory.Bean;
import umoo.wang.beanmanager.common.beanfactory.Inject;

/**
 * Created by yuanchen on 2019/02/22.
 */
@Bean
public class JedisConfiguration {

	@Bean
	public Jedis init(@Inject RedisConfig redisConfig) {
		return new Jedis(redisConfig.getHost(), redisConfig.getPort());
	}
}
