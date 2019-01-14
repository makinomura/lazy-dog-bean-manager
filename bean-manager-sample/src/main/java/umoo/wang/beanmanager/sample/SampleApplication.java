package umoo.wang.beanmanager.sample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import umoo.wang.beanmanager.client.BeanManager;

/**
 * Created by yuanchen on 2019/01/11.
 */
@EnableScheduling
@SpringBootApplication
public class SampleApplication {
	public static void main(String[] args) throws InterruptedException {
		SpringApplication app = new SpringApplication(SampleApplication.class);
		app.setWebApplicationType(WebApplicationType.NONE);

		ConfigurableApplicationContext context = app.run();

		BeanManager.start("localhost", 9999);
		Thread.sleep(10000);
	}
}
