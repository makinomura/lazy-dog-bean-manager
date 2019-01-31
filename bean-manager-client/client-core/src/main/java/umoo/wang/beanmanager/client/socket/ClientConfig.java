package umoo.wang.beanmanager.client.socket;

import umoo.wang.beanmanager.common.PropertyResolver;

/**
 * Created by yuanchen on 2019/01/23.
 */
public class ClientConfig {
	private static ClientConfig cache = null;
	private String host;
	private Integer port;
	private String appName;
	private String environmentName;

	public ClientConfig(String host, Integer port, String appName,
			String environmentName) {
		this.host = host;
		this.port = port;
		this.appName = appName;
		this.environmentName = environmentName;
	}

	public static ClientConfig read() {
		if (cache == null) {
			String host = PropertyResolver.read("lazydog.server.host");
			Integer port = PropertyResolver.read("lazydog.server.port",
					Integer.class);
			String appName = PropertyResolver.read("lazydog.app.name");
			String environmentName = PropertyResolver
					.read("lazydog.environment.name");

			cache = new ClientConfig(host, port, appName, environmentName);
		}

		return cache;
	}

	public String getHost() {
		return host;
	}

	public Integer getPort() {
		return port;
	}

	public String getAppName() {
		return appName;
	}

	public String getEnvironmentName() {
		return environmentName;
	}

	@Override
	public String toString() {
		return "ClientConfig{" + "host='" + host + '\'' + ", port=" + port
				+ ", appName='" + appName + '\'' + ", environmentName='"
				+ environmentName + '\'' + '}';
	}
}
