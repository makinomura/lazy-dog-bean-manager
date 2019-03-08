package umoo.wang.beanmanager.message.server.command;

import umoo.wang.beanmanager.message.Command;
import umoo.wang.beanmanager.message.CommandTargetEnum;
import umoo.wang.beanmanager.message.server.ServerCommandTypeEnum;
import umoo.wang.beanmanager.message.server.message.RegisterMessage;

/**
 * Created by yuanchen on 2019/01/23. 注册client命令
 */
public class RegisterCommand extends Command<RegisterMessage> {
	public RegisterCommand(RegisterMessage commandObj) {
		super(CommandTargetEnum.SERVER.value(),
				ServerCommandTypeEnum.REGISTER.value(), commandObj);
	}

	public RegisterCommand(int commandTarget, int commandType,
			RegisterMessage commandObj) {
		super(commandTarget, commandType, commandObj);
	}
}
