package umoo.wang.beanmanager.server.processor;

import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import umoo.wang.beanmanager.common.beanfactory.Bean;
import umoo.wang.beanmanager.message.Command;
import umoo.wang.beanmanager.message.CommandProcessor;
import umoo.wang.beanmanager.message.CommandTargetEnum;
import umoo.wang.beanmanager.message.client.ClientCommandTypeEnum;
import umoo.wang.beanmanager.message.server.ServerCommandTypeEnum;
import umoo.wang.beanmanager.message.server.message.ServerHeartBeatMessage;

import java.util.UUID;

/**
 * Created by yuanchen on 2019/01/23.
 */
@Bean
public class ServerHeartBeatCommandProcessor implements CommandProcessor {
	private final static Logger logger = LoggerFactory
			.getLogger(ServerHeartBeatCommandProcessor.class);

	@Override
	public boolean process(ChannelHandlerContext ctx, Command<?> command) {
		if (command.getCommandType() == ServerCommandTypeEnum.HEART_BEAT
				.value()) {
			long timestamp = ((ServerHeartBeatMessage) (command)
					.getCommandObj()).getTimestamp();

			logger.info("Receive heart-beat package from " + ctx.name()
					+ " ,duration :" + (System.currentTimeMillis() - timestamp)
					+ "ms");

			Command<?> result = Command.builder()
					.commandId(UUID.randomUUID().toString())
					.timestamps(System.currentTimeMillis())
					.replyTo(command.getCommandId())
					.commandTarget(CommandTargetEnum.CLIENT.value())
					.commandType(ClientCommandTypeEnum.ACK.value())
					.commandObj(200).build();

			ctx.writeAndFlush(result);

			return true;
		}

		return false;
	}
}
