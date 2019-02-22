package umoo.wang.beanmanager.cache;

import lombok.Data;
import umoo.wang.beanmanager.common.beanfactory.Bean;
import umoo.wang.beanmanager.common.beanfactory.Conf;

/**
 * Created by yuanchen on 2019/02/22.
 */
@Bean
@Data
public class RedisConfig {
	@Conf(key = "redis.host")
	private String host;

	@Conf(key = "redis.port", defaultValue = "6379")
	private int port;
}
