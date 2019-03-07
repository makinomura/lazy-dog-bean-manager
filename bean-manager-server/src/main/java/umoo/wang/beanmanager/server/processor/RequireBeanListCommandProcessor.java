package umoo.wang.beanmanager.server.processor;

import io.netty.channel.ChannelHandlerContext;
import umoo.wang.beanmanager.common.beanfactory.Bean;
import umoo.wang.beanmanager.common.beanfactory.Inject;
import umoo.wang.beanmanager.message.Command;
import umoo.wang.beanmanager.message.CommandProcessor;
import umoo.wang.beanmanager.message.CommandTargetEnum;
import umoo.wang.beanmanager.message.client.ClientCommandTypeEnum;
import umoo.wang.beanmanager.message.client.message.BeanListResMessage;
import umoo.wang.beanmanager.message.reply.ReplyableCommand;
import umoo.wang.beanmanager.message.server.ServerCommandTypeEnum;
import umoo.wang.beanmanager.message.server.message.BeanListReqMessage;
import umoo.wang.beanmanager.server.ClientManager;

import java.util.ArrayList;

/**
 * Created by yuanchen on 2019/03/05.
 */
@Bean
public class RequireBeanListCommandProcessor implements CommandProcessor {

	@Inject
	private ClientManager clientManager;

	@Override
	public boolean process(ChannelHandlerContext ctx, Command<?> command) {
		if (command
				.getCommandType() != ServerCommandTypeEnum.REQUIRE_CLIENT_BEAN_LIST
						.value()) {
			return false;
		}
		BeanListReqMessage commandObj = (BeanListReqMessage) command
				.getCommandObj();

		String channelKey = commandObj.getChannelKey();
		ClientManager.ContextWrapper context = clientManager
				.getContext(channelKey);

		if (context != null) {

			Command<?> requireBeanListCommand = Command.builderWithDefault()
					.commandTarget(CommandTargetEnum.CLIENT.value())
					.commandType(
							ClientCommandTypeEnum.REQUIRE_BEAN_LIST.value())
					.commandObj(200).build();

			context.getCtx().writeAndFlush(new ReplyableCommand<>(
					requireBeanListCommand, (success, result) -> {
						if (success) {
							BeanListResMessage message = (BeanListResMessage) result;

							Command<?> receiveBeanListCommand = Command
									.builderWithDefault()
									.replyTo(command.getCommandId())
									.commandTarget(
											CommandTargetEnum.CLIENT.value())
									.commandType(
											ClientCommandTypeEnum.RECEIVE_BEAN_LIST
													.value())
									.commandObj(message).build();
							ctx.writeAndFlush(receiveBeanListCommand);
						} else {
							failed(ctx, command);
						}
					}));
		} else {
			failed(ctx, command);
		}

		return true;
	}

	private void failed(ChannelHandlerContext ctx, Command<?> command) {
		Command<?> receiveBeanListCommand = Command.builderWithDefault()
				.replyTo(command.getCommandId())
				.commandTarget(CommandTargetEnum.CLIENT.value())
				.commandType(ClientCommandTypeEnum.RECEIVE_BEAN_LIST.value())
				.commandObj(new BeanListResMessage(new ArrayList<>())).build();
		ctx.writeAndFlush(receiveBeanListCommand);
	}
}
