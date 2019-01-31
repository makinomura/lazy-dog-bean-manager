package umoo.wang.beanmanager.server;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import umoo.wang.beanmanager.common.beanfactory.Inject;
import umoo.wang.beanmanager.message.Command;

/**
 * Created by yuanchen on 2019/01/11. 主要handler 保存/移除client的channel实体
 */
@ChannelHandler.Sharable
public class MainInHandler extends SimpleChannelInboundHandler {
	private final static Logger logger = LoggerFactory.getLogger(Server.class);

	static ChannelGroup channels = new DefaultChannelGroup("clients",
			GlobalEventExecutor.INSTANCE);

	@Inject
	private ServerCommandProcessor commandProcessor;

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		ClientManager.register(ctx);
		channels.add(ctx.channel());
	}

	@Override
	public void channelRead0(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		Command command = (Command) msg;

		// 转发消息到消息处理器
		commandProcessor.process(ctx, command);
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		ClientManager.unregister(ctx);
		channels.remove(ctx.channel());
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		cause.printStackTrace();
	}

}
