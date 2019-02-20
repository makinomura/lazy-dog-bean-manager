package umoo.wang.beanmanager.server.processor;

import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import umoo.wang.beanmanager.common.beanfactory.Bean;
import umoo.wang.beanmanager.message.Command;
import umoo.wang.beanmanager.message.CommandProcessor;
import umoo.wang.beanmanager.message.server.ServerCommandTypeEnum;

/**
 * Created by yuanchen on 2019/01/23.
 */
@Bean
public class AckProcessor implements CommandProcessor {
	private final static Logger logger = LoggerFactory
			.getLogger(AckProcessor.class);

	@Override
	public boolean process(ChannelHandlerContext ctx, Command<?> command) {
		if (command.getCommandType() == ServerCommandTypeEnum.ACK.value()) {
			logger.info("Receive ACK package from " + ctx.name() + ", value: "
					+ command.getCommandObj().toString());

			return true;
		}

		return false;
	}
}
