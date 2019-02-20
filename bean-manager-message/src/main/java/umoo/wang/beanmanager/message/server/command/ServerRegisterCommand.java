package umoo.wang.beanmanager.message.server.command;

import umoo.wang.beanmanager.message.Command;
import umoo.wang.beanmanager.message.CommandTargetEnum;
import umoo.wang.beanmanager.message.server.ServerCommandTypeEnum;
import umoo.wang.beanmanager.message.server.message.ServerRegisterMessage;

/**
 * Created by yuanchen on 2019/01/23. 注册client命令
 */
public class ServerRegisterCommand extends Command<ServerRegisterMessage> {
	public ServerRegisterCommand(ServerRegisterMessage commandObj) {
		super(CommandTargetEnum.SERVER.value(),
				ServerCommandTypeEnum.REGISTER.value(), commandObj);
	}

	public ServerRegisterCommand(int commandTarget, int commandType,
			ServerRegisterMessage commandObj) {
		super(commandTarget, commandType, commandObj);
	}
}
