package umoo.wang.beanmanager.common.beanfactory;

import umoo.wang.beanmanager.common.exception.ManagerException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yuanchen on 2019/01/15.
 */
@SuppressWarnings("unchecked")
public class SingletonBeanFactory implements BeanFactory {
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
	private Map<String, Object> beans = new HashMap<>();

	@Override
	public <T> T getBean(Class<T> clazz) {
		return (T) beans.get(clazz.getName());
	}

	@Override
	public <T> T newBean(Class<T> clazz, Object... args) {
		Object obj = null;

		Constructor constructor = findConstructor(clazz, args);
		if (constructor != null) {
			try {
				obj = constructor.newInstance(args);
			} catch (IllegalAccessException | InstantiationException
					| InvocationTargetException e) {
				throw new ManagerException("Create bean failed!", e);
			}
		}
		if (obj == null) {
			throw new ManagerException("No such constructors found!");
		}

		beans.put(clazz.getName(), obj);

		return (T) obj;
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
