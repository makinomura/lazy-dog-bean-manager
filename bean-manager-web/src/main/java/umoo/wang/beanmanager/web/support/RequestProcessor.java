package umoo.wang.beanmanager.web.support;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

/**
 * Created by yuanchen on 2019/01/16. Request处理器
 */
public interface RequestProcessor {
	/**
	 * 处理消息
	 * 
	 * @param ctx
	 *            channel实体
	 * @param request
	 *            请求
	 * @return 是否已被处理
	 */
	boolean process(ChannelHandlerContext ctx, FullHttpRequest request);
}
