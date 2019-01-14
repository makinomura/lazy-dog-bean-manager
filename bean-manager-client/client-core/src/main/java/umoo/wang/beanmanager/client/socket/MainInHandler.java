package umoo.wang.beanmanager.client.socket;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import umoo.wang.beanmanager.client.BeanManager;
import umoo.wang.beanmanager.common.util.EnumUtil;
import umoo.wang.beanmanager.message.Command;
import umoo.wang.beanmanager.message.CommandTargetEnum;
import umoo.wang.beanmanager.message.client.ClientCommandTypeEnum;
import umoo.wang.beanmanager.message.client.message.ClientFieldUpdateMessage;

/**
 * Created by yuanchen on 2019/01/11.
 */
public class MainInHandler extends ChannelInboundHandlerAdapter {

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		Command command = ((Command) msg);
		if (command == null) {
			return;// 未识别的消息，不处理
		}

		CommandTargetEnum commandTargetEnum = EnumUtil
				.valueOf(command.getCommandTarget(), CommandTargetEnum.class);
		if (commandTargetEnum != CommandTargetEnum.CLIENT) {
			return;
		}

		ClientCommandTypeEnum clientCommandTypeEnum = EnumUtil.valueOf(
				command.getCommandTarget(), ClientCommandTypeEnum.class);
		if (clientCommandTypeEnum != null) {
			switch (clientCommandTypeEnum) {
			case UPDATE_FIELD:
				ClientFieldUpdateMessage message = (ClientFieldUpdateMessage) command
						.getCommandObject();
				BeanManager.update(message.getFieldName(),
						message.getNewValue());
				break;
			default:
				break;
			}
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		cause.printStackTrace();
	}
}
