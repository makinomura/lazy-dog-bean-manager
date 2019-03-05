package umoo.wang.beanmanager.web.support;

import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;

/**
 * Created by yuanchen on 2019/01/30.
 */
public abstract class AbstractRequestProcessor implements RequestProcessor {

	protected ChannelHandlerContext ctx;
	protected FullHttpRequest request;

	public static FullHttpResponse json(ChannelHandlerContext ctx,
			FullHttpRequest request, HttpResponseStatus status, Object obj) {
		if (obj == null) {
			return new DefaultFullHttpResponse(request.protocolVersion(),
					status);
		}

		ByteBuf byteBuf = ByteBufUtil.writeUtf8(ctx.alloc(),
				JSON.toJSONString(obj));

		DefaultFullHttpResponse response = new DefaultFullHttpResponse(
				request.protocolVersion(), status, byteBuf, true);

		response.headers().set(HttpHeaderNames.CONTENT_TYPE,
				"application/json;charset=UTF-8");
		response.headers().set(HttpHeaderNames.CONTENT_LENGTH,
				byteBuf.readableBytes());
		response.headers().set(HttpHeaderNames.CONNECTION,
				HttpHeaderValues.KEEP_ALIVE);

		return response;
	}

	public abstract boolean support(FullHttpRequest request);

	public abstract FullHttpResponse process(FullHttpRequest request);

	@Override
	public boolean process(ChannelHandlerContext ctx, FullHttpRequest request) {
		this.ctx = ctx;
		this.request = request;

		if (!support(request)) {
			return false;
		}

		FullHttpResponse response = process(request);
		if (response != null) {
			ctx.writeAndFlush(response)
					.addListener(ChannelFutureListener.CLOSE);
		}
		return true;
	}

	public FullHttpResponse ok(Object obj) {
		return json(ctx, request, HttpResponseStatus.OK, obj);
	}

	public FullHttpResponse error(Object obj) {
		return json(ctx, request, HttpResponseStatus.INTERNAL_SERVER_ERROR,
				obj);
	}

	public FullHttpResponse notFound(Object obj) {
		return json(ctx, request, HttpResponseStatus.NOT_FOUND, obj);
	}
}
