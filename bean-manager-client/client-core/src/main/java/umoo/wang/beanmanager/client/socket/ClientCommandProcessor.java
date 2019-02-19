package umoo.wang.beanmanager.client.socket;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import umoo.wang.beanmanager.client.BeanManager;
import umoo.wang.beanmanager.common.beanfactory.Bean;
import umoo.wang.beanmanager.common.util.EnumUtil;
import umoo.wang.beanmanager.message.Command;
import umoo.wang.beanmanager.message.CommandProcessor;
import umoo.wang.beanmanager.message.CommandTargetEnum;
import umoo.wang.beanmanager.message.client.ClientCommandTypeEnum;
import umoo.wang.beanmanager.message.client.message.ClientFieldUpdateMessage;

import static umoo.wang.beanmanager.client.socket.Client.beanFactory;

/**
 * Created by yuanchen on 2019/01/16. Client消息处理器，考虑到后期命令会增多，需要改成链式调用
 */
@Bean
@ChannelHandler.Sharable
public class ClientCommandProcessor extends SimpleChannelInboundHandler<Command>
		implements CommandProcessor {

	private final static Logger logger = LoggerFactory
			.getLogger(ClientCommandProcessor.class);

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

	@Override
	public void channelRead0(ChannelHandlerContext ctx, Command command)
			throws Exception {
		process(ctx, command);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		logger.error(ctx.name(), cause);
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		logger.warn("Channel inactive, schedule to connect...");

		// 断线后重连
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
