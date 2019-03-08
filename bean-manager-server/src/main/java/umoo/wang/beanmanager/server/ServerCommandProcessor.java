package umoo.wang.beanmanager.server;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import umoo.wang.beanmanager.common.beanfactory.Bean;
import umoo.wang.beanmanager.common.beanfactory.BeanFactory;
import umoo.wang.beanmanager.common.beanfactory.Inject;
import umoo.wang.beanmanager.common.beanfactory.PostConstruct;
import umoo.wang.beanmanager.common.util.EnumUtil;
import umoo.wang.beanmanager.message.Command;
import umoo.wang.beanmanager.message.CommandProcessor;
import umoo.wang.beanmanager.message.CommandTargetEnum;
import umoo.wang.beanmanager.message.server.ServerCommandTypeEnum;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuanchen on 2019/01/16.
 */
@Slf4j
@Bean
@ChannelHandler.Sharable
public class ServerCommandProcessor extends SimpleChannelInboundHandler<Command>
		implements CommandProcessor {

	private List<CommandProcessor> processors = new ArrayList<>();

	@Inject
	private BeanFactory beanFactory;
	@Inject
	private ClientManager clientManager;

	@PostConstruct
	private void init() {
		beanFactory
				.listBean((bean) -> CommandProcessor.class
						.isAssignableFrom(bean.getClass())
						&& bean.getClass() != ServerCommandProcessor.class)
				.stream().map(bean -> (CommandProcessor) bean)
				.forEach(processors::add);
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
			log.warn("Unsupported command:" + command);
		}
		return processed;
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		clientManager.register(ctx);
	}

	@Override
	public void channelRead0(ChannelHandlerContext ctx, Command command)
			throws Exception {
		process(ctx, command);
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		clientManager.unregister(ctx);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		cause.printStackTrace();
	}
}
