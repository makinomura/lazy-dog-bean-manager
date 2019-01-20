package umoo.wang.beanmanager.common.cache;

import java.util.function.Function;

/**
 * Created by yuanchen on 2019/01/20. 本地缓存
 */
public interface Cache<K, V> {

	/**
	 * 获取
	 * 
	 * @param key
	 * @return
	 */
	V get(K key);

	/**
	 * 获取
	 * 
	 * @param key
	 *            键
	 * @param cacheLoader
	 *            缓存加载器
	 * @return
	 */
	V get(K key, Function<K, V> cacheLoader);

	/**
	 * 删除缓存
	 * 
	 * @param key
	 * @return
	 */
	V delete(K key);

	/**
	 * 设置缓存
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	V set(K key, V value);
}
