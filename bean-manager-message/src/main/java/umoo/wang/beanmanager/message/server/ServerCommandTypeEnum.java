package umoo.wang.beanmanager.message.server;

import umoo.wang.beanmanager.message.client.message.BeanListResMessage;
import umoo.wang.beanmanager.message.server.message.BeanListReqMessage;
import umoo.wang.beanmanager.message.server.message.HeartBeatMessage;
import umoo.wang.beanmanager.message.server.message.RegisterMessage;

/**
 * Created by yuanchen on 2019/01/14.
 */
public enum ServerCommandTypeEnum {
	ACK(0, Integer.class.getName()), HEART_BEAT(1,
			HeartBeatMessage.class.getName()), REGISTER(2,
					RegisterMessage.class.getName()), REQUIRE_CLIENT_BEAN_LIST(
							3,
							BeanListReqMessage.class
									.getName()), RECEIVE_CLIENT_BEAN_LIST(4,
											BeanListResMessage.class.getName());

	private String clazz;
	private int value;

	ServerCommandTypeEnum(int value, String clazz) {
		this.clazz = clazz;
		this.value = value;
	}

	public int value() {
		return this.value;
	}

	public String clazz() {
		return this.clazz;
	}
}