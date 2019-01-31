package umoo.wang.beanmanager.web.support;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import umoo.wang.beanmanager.common.beanfactory.Conf;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by yuanchen on 2019/01/31. 处理静态资源
 */
public class StaticResourceRequestProcessor extends AbstractRequestProcessor {

	@Conf("static.request.path")
	private String requestPath;
	@Conf("static.resource.path")
	private String resourcePath;

	@Override
	public boolean support(FullHttpRequest request) {
		return request.uri().startsWith(requestPath);
	}

	@Override
	public FullHttpResponse process(FullHttpRequest request) {
		String filePath = request.uri().substring(requestPath.length());
		InputStream resourceStream = Thread.currentThread()
				.getContextClassLoader()
				.getResourceAsStream(resourcePath + filePath);

		if (resourceStream == null) {
			return notFound(null);
		}
		ByteBuf buffer = ctx.alloc().buffer();

		try {
			int read = -1;

			while ((read = resourceStream.read()) != -1) {
				buffer.writeByte(read);
			}
		} catch (IOException e) {
			e.printStackTrace();
			return error(e.getMessage());
		}

		// TODO add Header

		return new DefaultFullHttpResponse(request.protocolVersion(),
				HttpResponseStatus.OK, buffer);
	}
}
