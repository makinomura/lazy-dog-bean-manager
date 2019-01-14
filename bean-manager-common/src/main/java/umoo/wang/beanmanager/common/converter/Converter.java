package umoo.wang.beanmanager.common.converter;

/**
 * Created by yuanchen on 2019/01/02.
 */
public interface Converter {
	<T> T convert(String value, Class<T> requireType);

	boolean support(Class<?> requireType);
}
