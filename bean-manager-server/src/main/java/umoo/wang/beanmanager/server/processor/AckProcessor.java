package umoo.wang.beanmanager.server.processor;

import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import umoo.wang.beanmanager.common.beanfactory.Bean;
import umoo.wang.beanmanager.message.Command;
import umoo.wang.beanmanager.message.CommandProcessor;
import umoo.wang.beanmanager.message.server.ServerCommandTypeEnum;

/**
 * Created by yuanchen on 2019/01/23.
 */
@Slf4j
@Bean
public class AckProcessor implements CommandProcessor {

	@Override
	public boolean process(ChannelHandlerContext ctx, Command<?> command) {
		if (command.getCommandType() == ServerCommandTypeEnum.ACK.value()) {
			log.info("Receive ACK package from " + ctx.name() + ", value: "
					+ command.getCommandObj().toString());

			return true;
		}

		return false;
	}
}
