package umoo.wang.beanmanager.common.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * Created by yuanchen on 2019/01/20.
 */
public class GenericLocalCache<K, V> implements Cache<K, V> {
	private Map<K, V> cache = new ConcurrentHashMap<>();

	@Override
	public V get(K key) {
		return cache.get(key);
	}

	@Override
	public V get(K key, Function<K, V> cacheLoader) {
		return cache.computeIfAbsent(key, cacheLoader);
	}

	@Override
	public V delete(K key) {
		return cache.remove(key);
	}

	@Override
	public V set(K key, V value) {
		return cache.replace(key, value);
	}
}
