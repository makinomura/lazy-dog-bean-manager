package umoo.wang.beanmanager.message.client.command;

import umoo.wang.beanmanager.message.Command;
import umoo.wang.beanmanager.message.CommandTargetEnum;
import umoo.wang.beanmanager.message.client.ClientCommandTypeEnum;
import umoo.wang.beanmanager.message.client.message.ClientFieldUpdateMessage;

/**
 * Created by yuanchen on 2019/01/14.
 */
public class ClientFieldUpdateCommand
		extends Command<ClientFieldUpdateMessage> {

	public ClientFieldUpdateCommand(ClientFieldUpdateMessage commandObj) {
		super(CommandTargetEnum.CLIENT.value(),
				ClientCommandTypeEnum.UPDATE_FIELD.value(), commandObj);
	}

	private ClientFieldUpdateCommand(int commandTarget, int commandType,
			ClientFieldUpdateMessage commandObj) {
		super(commandTarget, commandType, commandObj);
	}
}
