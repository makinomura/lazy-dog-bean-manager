package umoo.wang.beanmanager.common.converter;

/**
 * Created by yuanchen on 2019/01/02. 类型转换器
 */
public interface Converter {

	/**
	 * 转换String类型到指定类型
	 * 
	 * @param value
	 *            string
	 * @param requireType
	 *            所需类型
	 * @param <T>
	 * @return
	 */
	<T> T convert(String value, Class<T> requireType);

	/**
	 * 转换器是否支持给定类型的转换
	 * 
	 * @param requireType
	 *            所需类型
	 * @return
	 */
	boolean support(Class<?> requireType);
}
