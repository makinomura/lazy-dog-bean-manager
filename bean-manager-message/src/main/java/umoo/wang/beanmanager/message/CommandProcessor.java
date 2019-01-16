package umoo.wang.beanmanager.message;

import io.netty.channel.ChannelHandlerContext;

/**
 * Created by yuanchen on 2019/01/16. Command处理器
 */
public interface CommandProcessor {
	/**
	 * 处理消息
	 * 
	 * @param ctx
	 *            channel实体
	 * @param command
	 *            消息
	 * @return 是否已被处理
	 */
	boolean process(ChannelHandlerContext ctx, Command<?> command);
}
