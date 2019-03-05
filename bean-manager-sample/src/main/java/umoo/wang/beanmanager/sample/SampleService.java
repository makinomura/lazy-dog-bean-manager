package umoo.wang.beanmanager.sample;

import org.springframework.stereotype.Service;
import umoo.wang.beanmanager.client.FieldUpdateListener;
import umoo.wang.beanmanager.client.Manage;

import javax.annotation.PostConstruct;

/**
 * Created by yuanchen on 2019/01/11.
 */
@Manage(name = "sampleService")
@Service
public class SampleService implements FieldUpdateListener {

	@Manage(name = "time")
	private String time;

	@Override
	public void onUpdate(String fieldName, String newValue) {
		System.out.println("update " + fieldName + " to " + newValue
				+ ", now time is " + time);
	}

	@PostConstruct
	public void init() {
		time = String.valueOf(System.currentTimeMillis());
	}
}
