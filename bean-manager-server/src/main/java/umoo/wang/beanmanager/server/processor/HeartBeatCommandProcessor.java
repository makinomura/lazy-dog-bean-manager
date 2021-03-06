package umoo.wang.beanmanager.server.processor;

import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import umoo.wang.beanmanager.common.beanfactory.Bean;
import umoo.wang.beanmanager.message.Command;
import umoo.wang.beanmanager.message.CommandProcessor;
import umoo.wang.beanmanager.message.CommandTargetEnum;
import umoo.wang.beanmanager.message.client.ClientCommandTypeEnum;
import umoo.wang.beanmanager.message.server.ServerCommandTypeEnum;
import umoo.wang.beanmanager.message.server.message.HeartBeatMessage;

/**
 * Created by yuanchen on 2019/01/23.
 */
@Slf4j
@Bean
public class HeartBeatCommandProcessor implements CommandProcessor {

	@Override
	public boolean process(ChannelHandlerContext ctx, Command<?> command) {
		if (command.getCommandType() == ServerCommandTypeEnum.HEART_BEAT
				.value()) {
			long timestamp = ((HeartBeatMessage) (command)
					.getCommandObj()).getTimestamp();

			log.info("Receive heart-beat package from " + ctx.name()
					+ " ,duration :" + (System.currentTimeMillis() - timestamp)
					+ "ms");

			Command<?> result = Command.builderWithDefault()
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
