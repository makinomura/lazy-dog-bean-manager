package umoo.wang.beanmanager.message.reply;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import umoo.wang.beanmanager.common.beanfactory.Bean;
import umoo.wang.beanmanager.common.beanfactory.Inject;
import umoo.wang.beanmanager.common.util.StringUtil;
import umoo.wang.beanmanager.message.Command;

/**
 * Created by yuanchen on 2019/01/15. 如果消息是回复上一条消息，则调用callback
 */
@Bean
@ChannelHandler.Sharable
public class ReplyInvoker extends SimpleChannelInboundHandler<Command> {

	@Inject
	private ReplyRegister register;

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Command command)
			throws Exception {

		if (!StringUtil.isNullOrEmpty(command.getReplyTo())) {
			register.invokeCallback(command);
		} else {
			ctx.fireChannelRead(command);
		}
	}
}
