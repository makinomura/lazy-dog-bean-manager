package umoo.wang.beanmanager.message;

import io.netty.channel.ChannelHandlerContext;

/**
 * Created by yuanchen on 2019/01/16.
 */
public interface CommandProcessor {
	boolean process(ChannelHandlerContext ctx, Command<?> command);
}
