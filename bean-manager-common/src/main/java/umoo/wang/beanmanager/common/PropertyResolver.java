package umoo.wang.beanmanager.common;

import umoo.wang.beanmanager.common.converter.ConverterFactory;
import umoo.wang.beanmanager.common.exception.ManagerException;
import umoo.wang.beanmanager.common.exception.ServerException;
import umoo.wang.beanmanager.common.util.StringUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by yuanchen on 2019/01/14. 读取配置文件
 */
public class PropertyResolver {
	private final static String CONFIG_FILE_NAME = "config.properties";
	private final static Properties properties = new Properties();

	static {
		loadProperties();
	}

	/**
	 * 加载配置文件
	 */
	private static void loadProperties() {
		try {
			InputStream configStream = Thread.currentThread()
					.getContextClassLoader()
					.getResourceAsStream(CONFIG_FILE_NAME);
			properties.load(configStream);
		} catch (IOException e) {
			throw ServerException.wrap(e);
		}
	}

	/**
	 * 读取配置key
	 * 
	 * @param key
	 * @return
	 */
	public static String read(String key) {
		String value = properties.getProperty(key);

		if (StringUtil.isNullOrEmpty(value)) {
			throw new ManagerException("Property: " + key + " is not present.");
		}
		return value;
	}

	/**
	 * 读取配置key并转换成指定类型
	 * 
	 * @param key
	 * @param requireType
	 * @param <T>
	 * @return
	 */
	public static <T> T read(String key, Class<T> requireType) {
		return ConverterFactory.withType(requireType).convert(read(key),
				requireType);
	}
}
