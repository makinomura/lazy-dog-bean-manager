package umoo.wang.beanmanager.message.clientcommand;

/**
 * Created by yuanchen on 2019/01/14.
 */
public enum ClientCommandTypeEnum {

	UPDATE_FIELD(1, FieldUpdateMessage.class.getName());

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
