package umoo.wang.beanmanager.client.socket;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import umoo.wang.beanmanager.client.BeanManager;
import umoo.wang.beanmanager.common.util.EnumUtil;
import umoo.wang.beanmanager.message.Command;
import umoo.wang.beanmanager.message.CommandTargetEnum;
import umoo.wang.beanmanager.message.client.ClientCommandTypeEnum;
import umoo.wang.beanmanager.message.client.message.ClientFieldUpdateMessage;

import static umoo.wang.beanmanager.client.socket.Client.beanFactory;

/**
 * Created by yuanchen on 2019/01/11.
 */
@ChannelHandler.Sharable
public class MainInHandler extends SimpleChannelInboundHandler {

	private static final Logger logger = LoggerFactory
			.getLogger(MainInHandler.class);

	@Override
	public void channelRead0(ChannelHandlerContext ctx, Object msg)
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
						.getCommandObj();
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
		logger.error(ctx.name(), cause);
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		logger.warn("Channel inactive, schedule to connect...");

		ctx.channel().eventLoop().submit(() -> {
			try {
				Thread.sleep(5000L);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			beanFactory.getBean(Client.class).connect();
		});
	}
}
