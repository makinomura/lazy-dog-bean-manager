package umoo.wang.beanmanager.common.converter.impl;

import com.alibaba.fastjson.JSON;
import umoo.wang.beanmanager.common.converter.Converter;
import umoo.wang.beanmanager.common.exception.ManagerException;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Created by yuanchen on 2019/01/02.
 */
public class GenericConverterImpl implements Converter {

	public final static GenericConverterImpl instance = new GenericConverterImpl();

	@SuppressWarnings("unchecked")
	@Override
	public <T> T convert(String value, Class<T> requireType) {
		if (requireType == String.class) {
			return (T) value;
		} else {
			try {
				Method valueOf = requireType.getDeclaredMethod("valueOf",
						String.class);

				if (valueOf != null
						&& (valueOf.getModifiers() & Modifier.STATIC) != 0) {
					valueOf.setAccessible(true);

					return (T) valueOf.invoke(null, value);
				}

			} catch (Exception ignore) {
			}

			try {
				return JSON.parseObject(value, requireType);
			} catch (Exception ignore) {
			}
		}

		throw new ManagerException("Cannot covert '" + "' into type '"
				+ requireType.getName() + "'!");
	}

	@Override
	public boolean support(Class<?> requireType) {
		return true;
	}
}
