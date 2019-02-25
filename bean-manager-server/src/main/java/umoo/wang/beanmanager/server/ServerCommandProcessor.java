package umoo.wang.beanmanager.server;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@Bean
@ChannelHandler.Sharable
public class ServerCommandProcessor extends SimpleChannelInboundHandler<Command>
		implements CommandProcessor {
	private final static Logger logger = LoggerFactory
			.getLogger(ServerCommandProcessor.class);
	static ChannelGroup channels = new DefaultChannelGroup("clients",
			GlobalEventExecutor.INSTANCE);

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
			logger.warn("Unsupported command:" + command);
		}
		return processed;
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		clientManager.register(ctx);
		channels.add(ctx.channel());
	}

	@Override
	public void channelRead0(ChannelHandlerContext ctx, Command command)
			throws Exception {
		process(ctx, command);
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		clientManager.unregister(ctx);
		channels.remove(ctx.channel());
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		cause.printStackTrace();
	}
}
