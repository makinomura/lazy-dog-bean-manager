package umoo.wang.beanmanager.client.socket.processor;

import io.netty.channel.ChannelHandlerContext;
import umoo.wang.beanmanager.client.BeanManager;
import umoo.wang.beanmanager.common.beanfactory.Bean;
import umoo.wang.beanmanager.message.Command;
import umoo.wang.beanmanager.message.CommandProcessor;
import umoo.wang.beanmanager.message.CommandTargetEnum;
import umoo.wang.beanmanager.message.client.ClientCommandTypeEnum;
import umoo.wang.beanmanager.message.client.message.BeanListResMessage;
import umoo.wang.beanmanager.message.server.ServerCommandTypeEnum;

/**
 * Created by yuanchen on 2019/03/05.
 */
@Bean
public class RequireBeanListCommandProcessor implements CommandProcessor {
	@Override
	public boolean process(ChannelHandlerContext ctx, Command<?> command) {
		if (command.getCommandType() != ClientCommandTypeEnum.REQUIRE_BEAN_LIST
				.value()) {
			return false;
		}

		Command<?> receiveBeanListCommand = Command.builderWithDefault()
				.timestamps(System.currentTimeMillis())
				.commandTarget(CommandTargetEnum.SERVER.value())
				.commandType(
						ServerCommandTypeEnum.RECEIVE_CLIENT_BEAN_LIST.value())
				.commandObj(
						new BeanListResMessage(BeanManager.getBeanConfigs()))
				.build();
		ctx.writeAndFlush(receiveBeanListCommand);

		return true;
	}
}
