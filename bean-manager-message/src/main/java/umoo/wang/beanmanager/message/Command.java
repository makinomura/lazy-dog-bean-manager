package umoo.wang.beanmanager.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

/**
 * Created by yuanchen on 2019/01/14.
 */
@Data
@Builder
@AllArgsConstructor
public class Command<T> {
	private String commandId;
	private String replyTo;
	private long timestamps;
	private int commandTarget;
	private int commandType;
	private T commandObj;

	public Command(int commandTarget, int commandType, T commandObj) {
		this.commandId = UUID.randomUUID().toString();
		this.replyTo = "";
		this.timestamps = System.currentTimeMillis();
		this.commandTarget = commandTarget;
		this.commandType = commandType;
		this.commandObj = commandObj;
	}

	@Override
	public String toString() {
		return "Command{" + "commandId='" + commandId + '\'' + ", replyTo='"
				+ replyTo + '\'' + ", commandTarget=" + commandTarget
				+ ", commandType=" + commandType + ", commandObj=" + commandObj
				+ '}';
	}
}
