package umoo.wang.beanmanager.web;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import umoo.wang.beanmanager.common.beanfactory.Inject;
import umoo.wang.beanmanager.common.beanfactory.InjectBeanFactory;
import umoo.wang.beanmanager.common.beanfactory.SingletonBeanFactory;
import umoo.wang.beanmanager.web.controller.VersionController;
import umoo.wang.beanmanager.web.support.StaticResourceRequestProcessor;

/**
 * Created by yuanchen on 2019/01/30.
 */
public class WebServer {
	public final static InjectBeanFactory beanFactory = new InjectBeanFactory(
			new SingletonBeanFactory());
	private final static EventLoopGroup bossGroup = new NioEventLoopGroup(
			Runtime.getRuntime().availableProcessors() * 2);
	private final static EventLoopGroup workerGroup = new NioEventLoopGroup(
			100);

	private final static Logger logger = LoggerFactory
			.getLogger(WebServer.class);

	static {
		buildBeans();
	}

	@Inject
	private WebServerConfig config;

	/**
	 * 构建Beans
	 */
	private static void buildBeans() {
		beanFactory.createBean(MainInHandler.class);
		beanFactory.createBean(CoreRequestProcessor.class);
		beanFactory.createBean(StaticResourceRequestProcessor.class);
		beanFactory.createBean(VersionController.class);

		beanFactory.createBean(WebServerConfig.class);
		beanFactory.createBean(WebServer.class);

		beanFactory.doInject();
	}

	public static void main(String[] args) {
		beanFactory.getBean(WebServer.class).run();
	}

	public void run() {
		ServerBootstrap bootstrap = new ServerBootstrap();
		bootstrap.group(bossGroup, workerGroup)
				.channel(NioServerSocketChannel.class)
				.childHandler(new ChannelInitializer<SocketChannel>() {
					@Override
					protected void initChannel(SocketChannel channel)
							throws Exception {
						ChannelPipeline pipeline = channel.pipeline();
						pipeline.addLast(new HttpRequestDecoder());
						pipeline.addLast(new HttpObjectAggregator(65536));
						pipeline.addLast(new HttpResponseEncoder());
						pipeline.addLast(new ChunkedWriteHandler());
						pipeline.addLast(
								beanFactory.getBean(MainInHandler.class));
					}
				}).childOption(ChannelOption.SO_KEEPALIVE, true);

		try {
			ChannelFuture f = bootstrap.bind(config.getHost(),
					config.getPort());
			logger.info("WebServer start...");
			f.sync();
		} catch (InterruptedException e) {
			// 出错关闭bossGroup和workerGroup
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}
}
