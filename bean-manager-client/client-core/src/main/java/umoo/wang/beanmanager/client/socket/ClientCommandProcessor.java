package umoo.wang.beanmanager.client.socket;

import io.netty.channel.ChannelHandlerContext;
import umoo.wang.beanmanager.client.BeanManager;
import umoo.wang.beanmanager.common.beanfactory.Bean;
import umoo.wang.beanmanager.common.util.EnumUtil;
import umoo.wang.beanmanager.message.Command;
import umoo.wang.beanmanager.message.CommandProcessor;
import umoo.wang.beanmanager.message.CommandTargetEnum;
import umoo.wang.beanmanager.message.client.ClientCommandTypeEnum;
import umoo.wang.beanmanager.message.client.message.ClientFieldUpdateMessage;

/**
 * Created by yuanchen on 2019/01/16. Client消息处理器，考虑到后期命令会增多，需要改成链式调用
 */
@Bean
public class ClientCommandProcessor implements CommandProcessor {
	@Override
	public boolean process(ChannelHandlerContext ctx, Command<?> command) {

		if (command.getCommandTarget() != CommandTargetEnum.CLIENT.value()) {
			return false;
		}

		ClientCommandTypeEnum clientCommandTypeEnum = EnumUtil.valueOf(
				command.getCommandTarget(), ClientCommandTypeEnum.class);
		if (clientCommandTypeEnum == null) {
			return false;
		}
		switch (clientCommandTypeEnum) {
		case ACK:
			break;
		case UPDATE_FIELD:
			ClientFieldUpdateMessage message = (ClientFieldUpdateMessage) command
					.getCommandObj();
			BeanManager.update(message.getFieldName(), message.getNewValue());
			break;
		default:
			return false;
		}

		return true;
	}
}
