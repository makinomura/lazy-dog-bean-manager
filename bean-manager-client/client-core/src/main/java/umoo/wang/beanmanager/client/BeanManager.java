package umoo.wang.beanmanager.client;

import umoo.wang.beanmanager.common.converter.ConverterFactory;
import umoo.wang.beanmanager.client.socket.Client;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by yuanchen on 2019/01/11.
 */
@SuppressWarnings("unchecked")
public class BeanManager {
	private static List<BeanConfig> beanConfigs = new ArrayList<>();
	private static Map<String, List<FieldConfig>> fieldConfigMap = new HashMap<>();

	private static List<FieldUpdateListener> listeners = new ArrayList<>();

	public static void manage(Object bean) {
		BeanConfig<Object> beanConfig = buildBeanConfig(bean);

		if (beanConfig.getFieldConfigs().size() == 0) {
			return;
		}
		beanConfigs.add(beanConfig);

		for (FieldConfig<?, Object> fieldConfig : beanConfig
				.getFieldConfigs()) {
			if (!fieldConfig.getFieldName().equals(Manage.UNNAMED)) {
				List<FieldConfig> fieldConfigs = fieldConfigMap.computeIfAbsent(
						fieldConfig.getFieldName(), key -> new ArrayList<>());

				fieldConfigs.add(fieldConfig);
			}
		}
	}

	public static void registerListener(FieldUpdateListener listener) {
		listeners.add(listener);
	}

	public static int update(String fieldName, String newValue) {

		List<FieldConfig> fieldConfigs = fieldConfigMap.get(fieldName);

		int effectFields = 0;

		if (fieldConfigs != null && fieldConfigs.size() > 0) {
			for (FieldConfig fieldConfig : fieldConfigs) {
				Class clazz = fieldConfig.getClazz();
				fieldConfig.apply(ConverterFactory.withType(clazz)
						.convert(newValue, clazz));
			}

			effectFields = fieldConfigs.size();
		}

		listeners.forEach(listener -> listener.onUpdate(fieldName, newValue));
		return effectFields;
	}

	private static <T> BeanConfig<T> buildBeanConfig(T bean) {
		Class<T> clazz = (Class<T>) bean.getClass();

		List<FieldConfig<?, T>> fieldConfigs = new ArrayList<>();

		BeanConfig<T> beanConfig = new BeanConfig<>(Manage.UNNAMED, clazz, bean,
				fieldConfigs);

		Manage clazzManage = clazz.getAnnotation(Manage.class);
		if (clazzManage != null) {
			beanConfig.setBeanName(clazzManage.name());

			fieldConfigs.addAll(Arrays.stream(clazz.getDeclaredFields())
					.map(field -> getFieldConfig(beanConfig, field))
					.collect(Collectors.toList()));
		} else {
			fieldConfigs.addAll(Arrays.stream(clazz.getDeclaredFields())
					.filter(field -> field.getAnnotation(Manage.class) != null)
					.map(field -> getFieldConfig(beanConfig, field))
					.collect(Collectors.toList()));
		}

		return beanConfig;
	}

	private static <T> FieldConfig<Object, T> getFieldConfig(
			BeanConfig<T> beanConfig, Field field) {
		String fieldName = Manage.UNNAMED;
		Manage fieldManage = field.getAnnotation(Manage.class);
		if (fieldManage != null) {
			fieldName = fieldManage.name();
		}

		return new FieldConfig<>(fieldName, (Class<Object>) field.getType(),
				field, beanConfig);
	}

	public static void start(String host, int port) {
		Client.init(host, port);
	}
}
