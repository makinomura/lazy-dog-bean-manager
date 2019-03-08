package umoo.wang.beanmanager.message.client.command;

import umoo.wang.beanmanager.message.Command;
import umoo.wang.beanmanager.message.CommandTargetEnum;
import umoo.wang.beanmanager.message.client.ClientCommandTypeEnum;
import umoo.wang.beanmanager.message.client.message.FieldUpdateMessage;

/**
 * Created by yuanchen on 2019/01/14.
 */
public class FieldUpdateCommand extends Command<FieldUpdateMessage> {

	public FieldUpdateCommand(FieldUpdateMessage commandObj) {
		super(CommandTargetEnum.CLIENT.value(),
				ClientCommandTypeEnum.UPDATE_FIELD.value(), commandObj);
	}

	private FieldUpdateCommand(int commandTarget, int commandType,
			FieldUpdateMessage commandObj) {
		super(commandTarget, commandType, commandObj);
	}
}
