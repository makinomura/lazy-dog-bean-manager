package umoo.wang.beanmanager.common.beanfactory;

/**
 * Created by yuanchen on 2019/01/15.
 */
public interface BeanFactory {
	<T> T getBean(Class<T> clazz);

	<T> T newBean(Class<T> clazz, Object... args);
}
