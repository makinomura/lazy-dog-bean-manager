package umoo.wang.beanmanager.common.beanfactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import umoo.wang.beanmanager.common.PropertyResolver;
import umoo.wang.beanmanager.common.converter.ConverterFactory;
import umoo.wang.beanmanager.common.exception.ManagerException;
import umoo.wang.beanmanager.common.util.ClassUtil;

import java.lang.annotation.Annotation;
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
		// @Bean类
		Stream.of(rootPackageNames).forEach(this::doScan);
		delegate.getBean((bean) -> true).forEach(this::doInjectConf);
		delegate.getBean((bean) -> true)
				.forEach(bean -> doInjectBean(bean, false));

		// 扫描@Bean方法
		delegate.getBean((bean) -> true).forEach(this::createBeanFromMethod);
		delegate.getBean((bean) -> true)
				.forEach(bean -> doInjectBean(bean, true));

		delegate.getBean((bean) -> true).forEach(this::doPostConstruct);
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
	public Object createBean(Class<?> clazz, Object... args) {
		Object bean = delegate.createBean(clazz, args);

		doInjectConf(bean);
		doInjectBean(bean, true);
		doPostConstruct(bean);
		return bean;
	}

	private void doInjectBean(Object bean, boolean errorIfAbsent) {
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
				} else if (inject.required() && errorIfAbsent) {
					throw ManagerException.wrap(new RuntimeException(
							"Required inject field not exists. type: "
									+ requireType.getName()));
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
					throw ManagerException.wrap(new RuntimeException(
							"PostConstruct method should require 0 parameters."));
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

	private void createBeanFromMethod(Object bean) {
		for (Method method : bean.getClass().getDeclaredMethods()) {
			if (method.isAnnotationPresent(Bean.class)) {
				Object[] parameters = new Object[method.getParameterCount()];

				Class<?>[] parameterTypes = method.getParameterTypes();
				for (int i = 0; i < parameterTypes.length; i++) {
					Class<?> parameterType = parameterTypes[i];
					Annotation[] parameterAnnotation = method
							.getParameterAnnotations()[i];

					int finalI = i;
					Stream.of(parameterAnnotation)
							.filter(annotation -> annotation instanceof Inject)
							.findAny().ifPresent(annotation -> {
								parameters[finalI] = delegate
										.getBean(parameterType);
							});

					if (parameters[i] != null) {
						continue;
					}

					Stream.of(parameterAnnotation)
							.filter(annotation -> annotation instanceof Conf)
							.findAny().ifPresent(annotation -> {
								Conf annotationConf = (Conf) annotation;
								parameters[finalI] = PropertyResolver.read(
										annotationConf.key(), parameterType);
							});

					if (parameters[i] == null) {
						throw ManagerException.wrap(new RuntimeException(
								"@Inject or @Conf required for @Bean method parameters"));
					}
				}

				delegate.createBean(MethodFactoryBean.class, bean, method,
						parameters);
			}
		}
	}

	private void doScan(String rootPackageName) {
		ClassUtil
				.scan(Thread.currentThread().getContextClassLoader(),
						rootPackageName)
				.stream().filter(clazz -> clazz.isAnnotationPresent(Bean.class))
				.forEach(clazz -> delegate.createBean(clazz));
	}

	static class MethodFactoryBean implements FactoryBean {

		private Object obj;
		private Method method;
		private Object[] parameters;

		public MethodFactoryBean(Object obj, Method method,
				Object[] parameters) {
			this.obj = obj;
			this.method = method;
			this.parameters = parameters;
		}

		@Override
		public Object getBean() {
			try {
				return method.invoke(obj, parameters);
			} catch (IllegalAccessException | InvocationTargetException e) {
				e.printStackTrace();
				throw ManagerException.wrap(e);
			}
		}
	}
}
