package umoo.wang.beanmanager.server;

import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import umoo.wang.beanmanager.common.util.EnumUtil;
import umoo.wang.beanmanager.message.Command;
import umoo.wang.beanmanager.message.CommandProcessor;
import umoo.wang.beanmanager.message.CommandTargetEnum;
import umoo.wang.beanmanager.message.client.ClientCommandTypeEnum;
import umoo.wang.beanmanager.message.server.ServerCommandTypeEnum;
import umoo.wang.beanmanager.message.server.message.ServerHeartBeatMessage;

/**
 * Created by yuanchen on 2019/01/16.
 */
public class ServerCommandProcessor implements CommandProcessor {
	private final static Logger logger = LoggerFactory
			.getLogger(ServerCommandProcessor.class);

	@Override
	public boolean process(ChannelHandlerContext ctx, Command<?> command) {
		if (command.getCommandTarget() != CommandTargetEnum.SERVER.value()) {
			return false;
		}

		ServerCommandTypeEnum serverCommandTypeEnum = EnumUtil
				.valueOf(command.getCommandType(), ServerCommandTypeEnum.class);
		if (serverCommandTypeEnum == null) {
			return false;
		}

		Command<?> result = null;

		switch (serverCommandTypeEnum) {
		case ACK:
			logger.info("Receive ACK package from " + ctx.name() + ", value: "
					+ command.getCommandObj().toString());
			return true;
		case HEART_BEAT:
			long timestamp = ((ServerHeartBeatMessage) (command)
					.getCommandObj()).getTimestamp();

			logger.info("Receive heart-beat package from " + ctx.name()
					+ " ,duration :" + (System.currentTimeMillis() - timestamp)
					+ "ms");

			result = new Command<>(command.getCommandId(),
					CommandTargetEnum.CLIENT.value(),
					ClientCommandTypeEnum.ACK.value(), 200);
			break;
		default:
			return false;
		}

		ctx.writeAndFlush(result);
		return true;

	}
}