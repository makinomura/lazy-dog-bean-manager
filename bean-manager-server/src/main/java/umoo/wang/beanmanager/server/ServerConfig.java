package umoo.wang.beanmanager.server;

import lombok.Getter;
import lombok.ToString;
import umoo.wang.beanmanager.common.beanfactory.Conf;

/**
 * Created by yuanchen on 2019/01/23.
 */
@Getter
@ToString
public class ServerConfig {

	@Conf("lazydog.server.host")
	private String host;
	@Conf("lazydog.server.port")
	private Integer port;
}
