package umoo.wang.beanmanager.common.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by yuanchen on 2019/01/14.
 */
public class EnumUtil {

	public static <T extends Enum<T>> T valueOf(int value, Class<T> clazz) {

		try {
			for (T enumConstant : clazz.getEnumConstants()) {
				Method method = clazz.getMethod("value");

				int i = (int) method.invoke(enumConstant);

				if (i == value) {
					return enumConstant;
				}
			}
		} catch (IllegalAccessException | NoSuchMethodException
				| InvocationTargetException e) {
			e.printStackTrace();
		}

		return null;
	}
}
