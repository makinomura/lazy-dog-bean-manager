package umoo.wang.beanmanager.web;

import lombok.Getter;
import lombok.ToString;
import umoo.wang.beanmanager.common.beanfactory.Bean;
import umoo.wang.beanmanager.common.beanfactory.Conf;

/**
 * Created by yuanchen on 2019/01/23.
 */
@Bean
@Getter
@ToString
public class WebServerConfig {

	@Conf("lazydog.web.server.host")
	private String host;
	@Conf("lazydog.web.server.port")
	private Integer port;
}
