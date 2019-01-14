package umoo.wang.beanmanager.message;

/**
 * Created by yuanchen on 2019/01/14.
 */
public class Command<T> {
	private int commandTarget;
	private int commandType;
	private T commandObj;

	public Command(int commandTarget, int commandType, T commandObj) {
		this.commandObj = commandObj;
		this.commandTarget = commandTarget;
		this.commandType = commandType;
	}

	public int getCommandTarget() {
		return commandTarget;
	}

	public int getCommandType() {
		return commandType;
	}

	public T getCommandObject() {
		return commandObj;
	}
}
