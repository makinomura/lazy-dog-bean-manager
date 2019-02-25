package umoo.wang.beanmanager.common.converter;

import umoo.wang.beanmanager.common.beanfactory.BeanFactory;
import umoo.wang.beanmanager.common.beanfactory.SingletonBeanFactory;
import umoo.wang.beanmanager.common.converter.impl.GenericConverterImpl;
import umoo.wang.beanmanager.common.exception.ManagerException;

import java.util.List;
import java.util.function.Predicate;

/**
 * Created by yuanchen on 2019/01/10. 转换器工厂类
 */
public class ConverterFactory implements BeanFactory {
	// 代理对象
	private final static BeanFactory delegate = new SingletonBeanFactory();

	static {
		register(GenericConverterImpl.class);
	}

	/**
	 * 注册转换器
	 * 
	 * @param clazz
	 * @param <T>
	 * @return
	 */
	public static <T extends Converter> Converter register(Class<T> clazz) {
		return (Converter) delegate.createBean(clazz);
	}

	/**
	 * 获取支持指定类型值的转换器
	 * 
	 * @param requireType
	 * @return
	 */
	public static Converter withType(Class<?> requireType) {
		List<Object> converters = delegate
				.listBean(obj -> ((Converter) obj).support(requireType));

		if (!converters.isEmpty()) {
			return (Converter) converters.get(0);
		}

		throw ManagerException.wrap(new RuntimeException(
				"No converters found for type: " + requireType.getName()));
	}

	@Override
	public <T> T getBean(Class<T> clazz) {
		return delegate.getBean(clazz);
	}

	@Override
	public List<Object> listBean(Predicate<Object> predicate) {
		return delegate.listBean(predicate);
	}

	@Override
	public Object createBean(Class<?> clazz, Object... args) {
		if (!Converter.class.isAssignableFrom(clazz)) {
			throw ManagerException.wrap(
					new RuntimeException("Only support converters bean."));
		}

		return delegate.createBean(clazz, args);
	}
}
