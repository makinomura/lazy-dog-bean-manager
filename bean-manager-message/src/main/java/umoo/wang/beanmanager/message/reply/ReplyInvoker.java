package umoo.wang.beanmanager.message.reply;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import umoo.wang.beanmanager.common.util.StringUtil;
import umoo.wang.beanmanager.message.Command;

/**
 * Created by yuanchen on 2019/01/15.
 */
@ChannelHandler.Sharable
public class ReplyInvoker extends SimpleChannelInboundHandler {

	private ReplyRegister register;

	public ReplyInvoker(ReplyRegister register) {
		this.register = register;
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		Command command = (Command) msg;

		if (!StringUtil.isNullOrEmpty(command.getReplyTo())) {
			register.invokeCallback(command);
		} else {
			ctx.fireChannelRead(msg);
		}
	}
}