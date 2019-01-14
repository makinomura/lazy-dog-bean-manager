package umoo.wang.beanmanager.message;

/**
 * Created by yuanchen on 2019/01/14.
 */
public enum CommandTargetEnum {
	CLIENT(1), SERVER(2);
	private int value;

	CommandTargetEnum(int value) {
		this.value = value;
	}

	public int value() {
		return this.value;
	}
}