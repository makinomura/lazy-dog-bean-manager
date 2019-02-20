package umoo.wang.beanmanager.server;

/**
 * Created by yuanchen on 2019/01/23.
 */
public enum ClientStatusEnum {
	CONNECTED(0), REGISTERED(2);
	private int value;

	ClientStatusEnum(int value) {
		this.value = value;
	}

	public int value() {
		return this.value;
	}
}