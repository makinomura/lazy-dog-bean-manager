package umoo.wang.beanmanager.common.beanfactory;

import umoo.wang.beanmanager.common.exception.ManagerException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by yuanchen on 2019/01/15. 单例对象工厂
 */
@SuppressWarnings("unchecked")
public class SingletonBeanFactory implements BeanFactory {

	// Java值类型和包装类
	private static Map<String, Class<?>> wrapperMap = new HashMap<>();

	static {
		wrapperMap.put(Boolean.TYPE.getName(), Boolean.class);
		wrapperMap.put(Character.TYPE.getName(), Character.class);
		wrapperMap.put(Byte.TYPE.getName(), Byte.class);
		wrapperMap.put(Short.TYPE.getName(), Short.class);
		wrapperMap.put(Integer.TYPE.getName(), Integer.class);
		wrapperMap.put(Long.TYPE.getName(), Long.class);
		wrapperMap.put(Float.TYPE.getName(), Float.class);
		wrapperMap.put(Double.TYPE.getName(), Double.class);
		wrapperMap.put(Void.TYPE.getName(), Void.class);
	}

	private Map<Class<?>, Object> beans = new HashMap<>();

	@Override
	public <T> T getBean(Class<T> clazz) {
		if (BeanFactory.class.isAssignableFrom(clazz)) {
			return (T) this;
		}

		T bean = (T) beans.get(clazz);

		// 如果直接类型查找不到就找子类型
		if (bean == null) {
			List<Object> beanList = beans.entrySet().stream()
					.filter(entry -> clazz.isAssignableFrom(entry.getKey()))
					.map(Map.Entry::getValue).collect(Collectors.toList());

			if (!beanList.isEmpty()) {
				bean = (T) beanList.get(0);
			}
		}

		return bean;
	}

	@Override
	public List<Object> listBean(Predicate<Object> predicate) {
		return beans.values().stream().filter(predicate)
				.collect(Collectors.toList());
	}

	@Override
	public Object createBean(Class<?> clazz, Object... args) {
		Object obj = null;

		// 使用构造函数创建Bean
		Constructor constructor = findConstructor(clazz, args);
		if (constructor != null) {
			try {
				obj = constructor.newInstance(args);
			} catch (IllegalAccessException | InstantiationException
					| InvocationTargetException e) {
				throw ManagerException.wrap(e);
			}
		}
		if (obj == null) {
			throw ManagerException
					.wrap(new RuntimeException("No such constructors found!"));
		}

		if (obj instanceof FactoryBean) {
			obj = ((FactoryBean) obj).getBean();
		}

		beans.put(obj.getClass(), obj);
		return obj;
	}

	private Class<?> getWrapper(Class<?> primitiveClazz) {
		return wrapperMap.get(primitiveClazz.getName());
	}

	private Constructor<?> findConstructor(Class<?> clazz, Object[] args) {
		outer: for (Constructor<?> constructor : clazz.getConstructors()) {
			if (constructor.getParameterCount() == args.length) {
				Class<?>[] parameterTypes = constructor.getParameterTypes();
				for (int i = 0; i < parameterTypes.length; i++) {
					Class<?> parameterType = parameterTypes[i];
					Class<?> argvClazz = args[i].getClass();

					// 值类型转化为包装类比较
					if (parameterType.isPrimitive()) {
						parameterType = getWrapper(parameterType);
					}

					if (argvClazz.isPrimitive()) {
						argvClazz = getWrapper(argvClazz);
					}

					if (parameterType != argvClazz
							&& !parameterType.isAssignableFrom(argvClazz)) {
						continue outer;
					}
				}

				return constructor;
			}
		}

		return null;
	}
}
