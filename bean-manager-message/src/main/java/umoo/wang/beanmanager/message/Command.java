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
	// 消息ID
	private String commandId;
	// 回复消息ID
	private String replyTo;
	// 消息生成时间
	private long timestamps;
	// 命令发送对象
	private int commandTarget;
	// 命令类型
	private int commandType;
	// 消息实体
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
