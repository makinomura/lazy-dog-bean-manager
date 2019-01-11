package umoo.wang.beanmanager.client.converter;

import umoo.wang.beanmanager.client.converter.impl.GenericConverterImpl;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yuanchen on 2019/01/10.
 */
public class ConverterFactory {
	private static Map<String, Converter> converterMap = new HashMap<>();

	static {
		build(GenericConverterImpl.class);
	}

	public static <T extends Converter> Converter build(Class<T> clazz) {
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

		throw new RuntimeException();
	}
}
