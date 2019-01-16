package umoo.wang.beanmanager.client.springsupport;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import umoo.wang.beanmanager.client.BeanManager;
import umoo.wang.beanmanager.client.FieldUpdateListener;

/**
 * Created by yuanchen on 2019/01/11. 自动注册spring的Bean到BeanManager
 */
@Configuration
public class BeanManagerAutoConfiguration {
	@Bean
	public BeanPostProcessor scan() {
		return new BeanPostProcessor() {
			@Override
			public Object postProcessAfterInitialization(Object bean,
					String beanName) throws BeansException {

				// 注册FieldUpdateListener
				if (bean instanceof FieldUpdateListener) {
					BeanManager.registerListener(((FieldUpdateListener) bean));
				}

				// 添加Bean
				BeanManager.manage(bean);
				return bean;
			}
		};
	}

	@Bean
	public ApplicationListener startBeanManager() {
		return new ApplicationListener();
	}
}
