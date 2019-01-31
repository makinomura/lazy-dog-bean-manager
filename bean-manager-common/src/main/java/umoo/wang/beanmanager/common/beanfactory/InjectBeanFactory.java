package umoo.wang.beanmanager.common.beanfactory;

import umoo.wang.beanmanager.common.exception.ManagerException;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.function.Predicate;

/**
 * Created by yuanchen on 2019/01/30. 支持依赖注入和初始化的BeanFactory
 */
public class InjectBeanFactory implements BeanFactory {
	private BeanFactory delegate;

	public InjectBeanFactory(BeanFactory delegate) {
		this.delegate = delegate;
	}

	@Override
	public <T> T getBean(Class<T> clazz) {
		return delegate.getBean(clazz);
	}

	@Override
	public List<Object> getBean(Predicate<Object> predicate) {
		return delegate.getBean(predicate);
	}

	@Override
	public <T> T createBean(Class<T> clazz, Object... args) {
		return delegate.createBean(clazz, args);
	}

	private void doInject(Object bean) {
		Field[] declaredFields = bean.getClass().getDeclaredFields();

		for (Field field : declaredFields) {
			field.setAccessible(true);

			Inject inject = field.getAnnotation(Inject.class);
			if (inject != null) {
				Class<?> requireType = field.getType();

				Object requireBean = delegate.getBean(requireType);
				if (requireBean != null) {
					try {
						field.set(bean, requireBean);
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				} else if (inject.required()) {
					throw new ManagerException(
							"Required inject field not exists.");
				}
			}
		}
	}

	private void doPostConstruct(Object bean) {
		Method[] declaredMethods = bean.getClass().getDeclaredMethods();

		for (Method method : declaredMethods) {
			method.setAccessible(true);

			if (method.getAnnotation(PostConstruct.class) != null) {
				if (method.getParameterCount() > 0) {
					throw new ManagerException(
							"PostConstruct method should require 0 parameters.");
				}

				try {
					method.invoke(bean);
				} catch (IllegalAccessException | InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void doInject() {
		List<Object> beans = delegate.getBean((bean) -> true);
		beans.forEach(this::doInject);
		beans.forEach(this::doPostConstruct);
	}
}
