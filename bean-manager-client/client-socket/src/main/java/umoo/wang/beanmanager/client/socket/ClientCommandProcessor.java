package umoo.wang.beanmanager.client.socket;

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
import umoo.wang.beanmanager.message.client.ClientCommandTypeEnum;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuanchen on 2019/01/16. Client消息处理器，考虑到后期命令会增多，需要改成链式调用
 */
@Slf4j
@Bean
@ChannelHandler.Sharable
public class ClientCommandProcessor extends SimpleChannelInboundHandler<Command>
		implements CommandProcessor {

	private List<CommandProcessor> processors = new ArrayList<>();

	@Inject
	private BeanFactory beanFactory;

	@PostConstruct
	private void init() {
		beanFactory
				.listBean((bean) -> CommandProcessor.class
						.isAssignableFrom(bean.getClass())
						&& bean.getClass() != ClientCommandProcessor.class)
				.stream().map(bean -> (CommandProcessor) bean)
				.forEach(processors::add);
	}

	@Override
	public boolean process(ChannelHandlerContext ctx, Command<?> command) {

		if (command.getCommandTarget() != CommandTargetEnum.CLIENT.value()) {
			return false;
		}

		ClientCommandTypeEnum clientCommandTypeEnum = EnumUtil.valueOf(
				command.getCommandTarget(), ClientCommandTypeEnum.class);
		if (clientCommandTypeEnum == null) {
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
	public void channelRead0(ChannelHandlerContext ctx, Command command)
			throws Exception {
		process(ctx, command);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		log.error(ctx.name(), cause);
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		log.warn("Channel inactive, schedule to connect...");

		// 断线后重连
		ctx.channel().eventLoop().submit(() -> {
			try {
				Thread.sleep(5000L);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			beanFactory.getBean(Client.class).connect();
		});
	}
}
