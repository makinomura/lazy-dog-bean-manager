package umoo.wang.beanmanager.client.socket;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import umoo.wang.beanmanager.message.Command;
import umoo.wang.beanmanager.message.CommandProcessor;

import static umoo.wang.beanmanager.client.socket.Client.beanFactory;

/**
 * Created by yuanchen on 2019/01/11.
 */
@ChannelHandler.Sharable
public class MainInHandler extends SimpleChannelInboundHandler {

	private final static Logger logger = LoggerFactory
			.getLogger(MainInHandler.class);

	private CommandProcessor commandProcessor;

	public MainInHandler(CommandProcessor commandProcessor) {
		this.commandProcessor = commandProcessor;
	}

	@Override
	public void channelRead0(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		Command command = ((Command) msg);
		commandProcessor.process(ctx, command);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		logger.error(ctx.name(), cause);
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		logger.warn("Channel inactive, schedule to connect...");

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
