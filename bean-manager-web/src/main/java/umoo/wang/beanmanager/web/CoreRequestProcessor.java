package umoo.wang.beanmanager.web;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import umoo.wang.beanmanager.common.beanfactory.Bean;
import umoo.wang.beanmanager.common.beanfactory.BeanFactory;
import umoo.wang.beanmanager.common.beanfactory.Inject;
import umoo.wang.beanmanager.common.beanfactory.PostConstruct;
import umoo.wang.beanmanager.web.support.AbstractRequestProcessor;
import umoo.wang.beanmanager.web.support.ControllerAdaptor;
import umoo.wang.beanmanager.web.support.RequestProcessor;
import umoo.wang.beanmanager.web.support.StaticResourceRequestProcessor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuanchen on 2019/01/30.
 */
@Bean
public class CoreRequestProcessor implements RequestProcessor {

	private final static Logger logger = LoggerFactory
			.getLogger(CoreRequestProcessor.class);
	private static RequestProcessor processor404 = new DefaultRequestProcessor(
			HttpResponseStatus.NOT_FOUND);
	private static RequestProcessor processor500 = new DefaultRequestProcessor(
			HttpResponseStatus.INTERNAL_SERVER_ERROR);
	private List<RequestProcessor> processors = new ArrayList<>();

	@Inject
	private BeanFactory beanFactory;
	@Inject
	private StaticResourceRequestProcessor staticResourceRequestProcessor;

	@PostConstruct
	private void init() {
		processors.add(staticResourceRequestProcessor);
		beanFactory
				.getBean((bean) -> bean.getClass().getName()
						.endsWith("Controller"))
				.stream().map(ControllerAdaptor::new).forEach(processors::add);
	}

	@Override
	public boolean process(ChannelHandlerContext ctx, FullHttpRequest request) {
		boolean processed = false;

		for (RequestProcessor processor : processors) {
			try {
				processed = processor.process(ctx, request);
			} catch (Exception e) {
				e.printStackTrace();
				processed = processor500.process(ctx, request);
			}

			if (processed) {
				break;
			}
		}

		if (!processed) {
			processed = processor404.process(ctx, request);
		}
		return processed;
	}

	private static class DefaultRequestProcessor
			extends AbstractRequestProcessor {

		private HttpResponseStatus status;

		DefaultRequestProcessor(HttpResponseStatus status) {
			this.status = status;
		}

		@Override
		public boolean support(FullHttpRequest request) {
			return true;
		}

		@Override
		public FullHttpResponse process(FullHttpRequest request) {
			return new DefaultFullHttpResponse(request.protocolVersion(),
					status);
		}
	}
}
