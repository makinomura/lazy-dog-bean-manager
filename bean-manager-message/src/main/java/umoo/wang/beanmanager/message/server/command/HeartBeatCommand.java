package umoo.wang.beanmanager.message.server.command;

import umoo.wang.beanmanager.message.Command;
import umoo.wang.beanmanager.message.CommandTargetEnum;
import umoo.wang.beanmanager.message.server.ServerCommandTypeEnum;
import umoo.wang.beanmanager.message.server.message.HeartBeatMessage;

/**
 * Created by yuanchen on 2019/01/14.
 */
public class HeartBeatCommand extends Command<HeartBeatMessage> {

	public HeartBeatCommand(HeartBeatMessage commandObj) {
		this(CommandTargetEnum.SERVER.value(),
				ServerCommandTypeEnum.HEART_BEAT.value(), commandObj);
	}

	private HeartBeatCommand(int commandTarget, int commandType,
			HeartBeatMessage commandObj) {
		super(commandTarget, commandType, commandObj);
	}
}
