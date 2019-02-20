package umoo.wang.beanmanager.common.beanfactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import umoo.wang.beanmanager.common.PropertyResolver;
import umoo.wang.beanmanager.common.converter.ConverterFactory;
import umoo.wang.beanmanager.common.exception.ManagerException;
import umoo.wang.beanmanager.common.util.ClassUtil;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Created by yuanchen on 2019/01/30. 支持依赖注入和初始化的BeanFactory
 */
public class InjectBeanFactory implements BeanFactory {

	private final static Logger logger = LoggerFactory
			.getLogger(InjectBeanFactory.class);

	private BeanFactory delegate;

	public InjectBeanFactory(BeanFactory delegate, String... rootPackageNames) {
		this.delegate = delegate;

		logger.info("Scanning beans in package {}",
				String.join(" , ", Arrays.asList(rootPackageNames)));
		Stream.of(rootPackageNames).forEach(this::doScan);
		doInject();
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

	private void doInjectBean(Object bean) {
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

	private void doInjectConf(Object bean) {
		Field[] declaredFields = bean.getClass().getDeclaredFields();

		for (Field field : declaredFields) {
			field.setAccessible(true);

			Conf conf = field.getAnnotation(Conf.class);
			if (conf != null) {
				Class<?> requireType = field.getType();

				Object requireBean = PropertyResolver.read(conf.key(),
						requireType);

				if (requireBean == null
						&& !Conf.DEFAULT_NONE.equals(conf.defaultValue())) {
					requireBean = ConverterFactory.withType(requireType)
							.convert(conf.defaultValue(), requireType);
				}

				if (requireBean != null) {
					try {
						field.set(bean, requireBean);
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	private void doInject() {
		List<Object> beans = delegate.getBean((bean) -> true);
		beans.forEach(this::doInjectBean);
		beans.forEach(this::doInjectConf);
		beans.forEach(this::doPostConstruct);
	}

	private void doScan(String rootPackageName) {
		ClassUtil
				.scan(Thread.currentThread().getContextClassLoader(),
						rootPackageName)
				.stream().filter(clazz -> clazz.isAnnotationPresent(Bean.class))
				.forEach(clazz -> createBean(clazz));
	}
}
