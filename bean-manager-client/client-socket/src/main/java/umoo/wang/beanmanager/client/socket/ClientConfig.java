package umoo.wang.beanmanager.client.socket;

import lombok.Value;
import umoo.wang.beanmanager.common.beanfactory.Bean;
import umoo.wang.beanmanager.common.beanfactory.Conf;

/**
 * Created by yuanchen on 2019/01/23.
 */
@Bean
@Value
public class ClientConfig {

	@Conf(key = "lazydog.server.host")
	private String host;
	@Conf(key = "lazydog.server.port")
	private Integer port;
	@Conf(key = "lazydog.app.name")
	private String appName;
	@Conf(key = "lazydog.environment.name")
	private String environmentName;
	@Conf(key = "lazydog.app.admin", defaultValue = "false")
	private Boolean admin;
}
