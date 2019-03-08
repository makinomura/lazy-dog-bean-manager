package umoo.wang.beanmanager.message.client;

import umoo.wang.beanmanager.message.client.message.BeanListResMessage;
import umoo.wang.beanmanager.message.client.message.FieldUpdateMessage;

/**
 * Created by yuanchen on 2019/01/14.
 */
public enum ClientCommandTypeEnum {
	ACK(0, Integer.class.getName()), UPDATE_FIELD(1,
			FieldUpdateMessage.class.getName()), REQUIRE_BEAN_LIST(2,
					Integer.class.getName()), RECEIVE_BEAN_LIST(3,
							BeanListResMessage.class.getName());

	private String clazz;
	private int value;

	ClientCommandTypeEnum(int value, String clazz) {
		this.clazz = clazz;
		this.value = value;
	}

	public String clazz() {
		return clazz;
	}

	public int value() {
		return value;
	}
}
