package umoo.wang.beanmanager.common.beanfactory;

import java.util.List;
import java.util.function.Predicate;

/**
 * Created by yuanchen on 2019/01/15.
 */
public interface BeanFactory {

	/**
	 * 获取Bean
	 * 
	 * @param clazz
	 * @param <T>
	 * @return
	 */
	<T> T getBean(Class<T> clazz);

	/**
	 * 获取Bean
	 * 
	 * @param predicate
	 * @return
	 */
	List<Object> getBean(Predicate<Object> predicate);

	/**
	 * 创建新的Bean并放入工厂
	 * 
	 * @param clazz
	 *            类
	 * @param args
	 *            构造函数参数
	 * @param <T>
	 * @return
	 */
	<T> T createBean(Class<T> clazz, Object... args);
}