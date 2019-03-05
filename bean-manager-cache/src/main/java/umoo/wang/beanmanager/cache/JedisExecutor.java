package umoo.wang.beanmanager.cache;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import umoo.wang.beanmanager.common.ResourceExecutor;
import umoo.wang.beanmanager.common.beanfactory.Bean;
import umoo.wang.beanmanager.common.beanfactory.Inject;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created by yuanchen on 2019/02/22.
 */
@Bean
public class JedisExecutor implements ResourceExecutor<Jedis> {

	@Inject
	private JedisPool jedisPool;

	@Bean
	public JedisPool pool(@Inject RedisConfig redisConfig) {
		return new JedisPool(new JedisPoolConfig(), redisConfig.getHost(),
				redisConfig.getPort());
	}

	@Override
	public Jedis getResource() {
		return jedisPool.getResource();
	}

	@Override
	public void execute(Consumer<Jedis> consumer) {
		execute((jedis) -> {
			consumer.accept(jedis);
			return null;
		});
	}

	@Override
	public <T> T execute(Function<Jedis, T> function) {
		try (Jedis jedis = getResource()) {
			return function.apply(jedis);
		}
	}
}
