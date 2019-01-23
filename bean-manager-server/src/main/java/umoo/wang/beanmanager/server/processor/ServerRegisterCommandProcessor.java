package umoo.wang.beanmanager.server.processor;

import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import umoo.wang.beanmanager.message.Command;
import umoo.wang.beanmanager.message.CommandProcessor;
import umoo.wang.beanmanager.message.server.ServerCommandTypeEnum;
import umoo.wang.beanmanager.message.server.message.ServerRegisterMessage;
import umoo.wang.beanmanager.server.ClientManager;
import umoo.wang.beanmanager.server.ClientStatusEnum;

/**
 * Created by yuanchen on 2019/01/23.
 */
public class ServerRegisterCommandProcessor implements CommandProcessor {
	private final static Logger logger = LoggerFactory
			.getLogger(ServerRegisterCommandProcessor.class);

	@Override
	public boolean process(ChannelHandlerContext ctx, Command<?> command) {

		if (command.getCommandType() == ServerCommandTypeEnum.REGISTER
				.value()) {

			logger.info("Client register:" + command.getCommandObj());

			// 更新Client状态
			ClientManager.updateStatus(ctx,
					ClientStatusEnum.REGISTERED.value());

			// 设置Client信息
			ClientManager.ClientConfig config = ClientManager.getConfig(ctx);
			ServerRegisterMessage msg = (ServerRegisterMessage) command
					.getCommandObj();
			config.setAppName(msg.getAppName());
			config.setEnvironment(msg.getEnvironmentName());
			return true;
		}

		return false;
	}
}
