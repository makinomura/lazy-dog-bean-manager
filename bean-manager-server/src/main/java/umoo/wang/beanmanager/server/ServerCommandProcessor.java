package umoo.wang.beanmanager.server;

import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import umoo.wang.beanmanager.common.util.EnumUtil;
import umoo.wang.beanmanager.message.Command;
import umoo.wang.beanmanager.message.CommandProcessor;
import umoo.wang.beanmanager.message.CommandTargetEnum;
import umoo.wang.beanmanager.message.server.ServerCommandTypeEnum;
import umoo.wang.beanmanager.server.processor.AckProcessor;
import umoo.wang.beanmanager.server.processor.ServerHeartBeatCommandProcessor;
import umoo.wang.beanmanager.server.processor.ServerRegisterCommandProcessor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuanchen on 2019/01/16.
 */
public class ServerCommandProcessor implements CommandProcessor {
	private final static Logger logger = LoggerFactory
			.getLogger(ServerCommandProcessor.class);

	private static List<CommandProcessor> processors = new ArrayList<>();

	static {
		processors.add(new AckProcessor());
		processors.add(new ServerHeartBeatCommandProcessor());
		processors.add(new ServerRegisterCommandProcessor());
	}

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

		boolean processed = false;

		for (CommandProcessor processor : processors) {
			processed = processor.process(ctx, command);

			if (processed) {
				break;
			}
		}

		if (!processed) {
			logger.warn("Unsupported command:" + command);
		}
		return processed;
	}
}
