package umoo.wang.beanmanager.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import umoo.wang.beanmanager.message.Command;
import umoo.wang.beanmanager.message.CommandTargetEnum;
import umoo.wang.beanmanager.message.server.ServerCommandTypeEnum;
import umoo.wang.beanmanager.message.server.message.ServerHeartBeatMessage;

import java.net.InetSocketAddress;

/**
 * Created by yuanchen on 2019/01/11.
 */
public class MainInHandler extends ChannelInboundHandlerAdapter {
	ChannelGroup channels = new DefaultChannelGroup("clients",
			GlobalEventExecutor.INSTANCE);

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		ContextHolder.contexts.put(buildContextKey(ctx), ctx);
		channels.add(ctx.channel());
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		Command command = (Command) msg;

		if (command.getCommandTarget() == CommandTargetEnum.SERVER.value()
				&& command.getCommandType() == ServerCommandTypeEnum.HEART_BEAT
						.value()) {
			long timestamp = ((ServerHeartBeatMessage) ((Command) msg)
					.getCommandObject()).getTimestamp();

			System.out.println("receive heart-beat package from "
					+ buildContextKey(ctx) + " ,duration :"
					+ (System.currentTimeMillis() - timestamp) + "ms");
		} else {
			System.out.println(command);
		}

	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		ContextHolder.contexts.remove(buildContextKey(ctx));
		channels.remove(ctx.channel());
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		cause.printStackTrace();
	}

	private String buildContextKey(ChannelHandlerContext ctx) {
		InetSocketAddress inetSocketAddress = (InetSocketAddress) ctx.channel()
				.remoteAddress();

		return inetSocketAddress.getHostString() + ":"
				+ inetSocketAddress.getPort();
	}
}
