package umoo.wang.beanmanager.server;

import umoo.wang.beanmanager.common.converter.ConverterFactory;
import umoo.wang.beanmanager.common.exception.ManagerException;
import umoo.wang.beanmanager.common.exception.ServerException;
import umoo.wang.beanmanager.common.util.StringUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by yuanchen on 2019/01/14.
 */
public class PropertyResolver {
	private final static String CONFIG_FILE_NAME = "config.properties";
	private final static Properties properties = new Properties();

	static {
		loadProperties();
	}

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

	public static String read(String key) {
		String value = properties.getProperty(key);

		if (StringUtil.isNullOrEmpty(value)) {
			throw new ManagerException("Property: " + key + " is not present.");
		}
		return value;
	}

	public static <T> T read(String key, Class<T> requireType) {
		return ConverterFactory.withType(requireType).convert(read(key),
				requireType);
	}
}
