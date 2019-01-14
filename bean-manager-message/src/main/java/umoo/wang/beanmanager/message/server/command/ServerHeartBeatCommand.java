package umoo.wang.beanmanager.message.server.command;

import umoo.wang.beanmanager.message.Command;
import umoo.wang.beanmanager.message.CommandTargetEnum;
import umoo.wang.beanmanager.message.server.ServerCommandTypeEnum;
import umoo.wang.beanmanager.message.server.message.ServerHeartBeatMessage;

/**
 * Created by yuanchen on 2019/01/14.
 */
public class ServerHeartBeatCommand extends Command<ServerHeartBeatMessage> {

	public ServerHeartBeatCommand(ServerHeartBeatMessage commandObj) {
		this(CommandTargetEnum.SERVER.value(),
				ServerCommandTypeEnum.HEART_BEAT.value(), commandObj);
	}

	private ServerHeartBeatCommand(int commandTarget, int commandType,
			ServerHeartBeatMessage commandObj) {
		super(commandTarget, commandType, commandObj);
	}
}
