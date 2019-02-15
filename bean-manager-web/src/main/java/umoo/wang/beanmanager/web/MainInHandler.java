package umoo.wang.beanmanager.web;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import umoo.wang.beanmanager.common.beanfactory.Bean;
import umoo.wang.beanmanager.common.beanfactory.Inject;

/**
 * Created by yuanchen on 2019/01/30.
 */
@Bean
@ChannelHandler.Sharable
public class MainInHandler
		extends SimpleChannelInboundHandler<FullHttpRequest> {

	@Inject
	private CoreRequestProcessor processor;

	@Override
	protected void channelRead0(ChannelHandlerContext ctx,
			FullHttpRequest request) throws Exception {

		processor.process(ctx, request);
	}
}
