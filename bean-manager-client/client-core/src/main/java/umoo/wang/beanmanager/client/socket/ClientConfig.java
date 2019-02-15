package umoo.wang.beanmanager.client.socket;

import umoo.wang.beanmanager.common.beanfactory.Bean;
import umoo.wang.beanmanager.common.beanfactory.Conf;

/**
 * Created by yuanchen on 2019/01/23.
 */
@Bean
public class ClientConfig {

	@Conf("lazydog.server.host")
	private String host;
	@Conf("lazydog.server.port")
	private Integer port;
	@Conf("lazydog.app.name")
	private String appName;
	@Conf("lazydog.environment.name")
	private String environmentName;

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
