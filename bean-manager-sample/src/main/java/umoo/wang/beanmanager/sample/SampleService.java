package umoo.wang.beanmanager.sample;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import umoo.wang.beanmanager.client.Manage;

/**
 * Created by yuanchen on 2019/01/11.
 */
@Service
public class SampleService {

	@Manage(name = "time")
	private String time;

	@Scheduled(cron = "* * * * * ?")
	public void print() {
		System.out.println(time);
	}
}
