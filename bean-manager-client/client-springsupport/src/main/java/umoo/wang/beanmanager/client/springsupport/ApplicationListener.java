package umoo.wang.beanmanager.client.springsupport;

import org.springframework.context.event.ContextRefreshedEvent;
import umoo.wang.beanmanager.client.socket.Client;
import umoo.wang.beanmanager.common.PropertyResolver;
import umoo.wang.beanmanager.common.exception.ClientException;

/**
 * Created by yuanchen on 2019/01/14.
 */
public class ApplicationListener implements
		org.springframework.context.ApplicationListener<ContextRefreshedEvent> {
	private boolean inited = false;

	@Override
	public void onApplicationEvent(
			ContextRefreshedEvent contextRefreshedEvent) {
		if (inited) {
			return;
		}

		try {
			String host = PropertyResolver.read("lazydog.server.host");
			Integer port = PropertyResolver.read("lazydog.server.port",
					Integer.class);

			Client.start(host, port);
			inited = true;
		} catch (Exception e) {
			throw ClientException.wrap(e);
		}
	}
}
