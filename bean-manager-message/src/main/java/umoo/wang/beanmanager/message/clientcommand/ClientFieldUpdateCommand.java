package umoo.wang.beanmanager.message.clientcommand;

import umoo.wang.beanmanager.message.Command;
import umoo.wang.beanmanager.message.CommandTargetEnum;

/**
 * Created by yuanchen on 2019/01/14.
 */
public class ClientFieldUpdateCommand extends Command<FieldUpdateMessage> {

	public ClientFieldUpdateCommand(FieldUpdateMessage commandObj) {
		super(CommandTargetEnum.CLIENT.value(),
				ClientCommandTypeEnum.UPDATE_FIELD.value(), commandObj);
	}

	private ClientFieldUpdateCommand(int commandTarget, int commandType,
			FieldUpdateMessage commandObj) {
		super(commandTarget, commandType, commandObj);
	}
}
