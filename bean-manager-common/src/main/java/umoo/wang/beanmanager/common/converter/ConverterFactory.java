package umoo.wang.beanmanager.common.converter;

import umoo.wang.beanmanager.common.converter.impl.GenericConverterImpl;
import umoo.wang.beanmanager.common.exception.ManagerException;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yuanchen on 2019/01/10.
 */
public class ConverterFactory {
	private static Map<String, Converter> converterMap = new HashMap<>();

	static {
		register(GenericConverterImpl.class);
	}

	public static <T extends Converter> Converter register(Class<T> clazz) {
		return converterMap.computeIfAbsent(clazz.getName(), name -> {
			try {
				return clazz.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		});
	}

	public static Converter withType(Class<?> requireType) {
		for (Converter converter : converterMap.values()) {
			if (converter.support(requireType)) {
				return converter;
			}
		}

		throw new ManagerException(
				"No converters found for type: " + requireType.getName());
	}
}
