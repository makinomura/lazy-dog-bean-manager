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
import lombok.extern.slf4j.Slf4j;
import umoo.wang.beanmanager.client.socket.Client;
import umoo.wang.beanmanager.common.beanfactory.Bean;
import umoo.wang.beanmanager.common.beanfactory.BeanFactory;
import umoo.wang.beanmanager.common.beanfactory.Inject;
import umoo.wang.beanmanager.common.beanfactory.InjectBeanFactory;
import umoo.wang.beanmanager.common.beanfactory.SingletonBeanFactory;

/**
 * Created by yuanchen on 2019/01/30.
 */
@Slf4j
@Bean
public class WebServer {
	private final static String[] ROOT_PACKAGE_NAMES = {
			"umoo.wang.beanmanager.web", "umoo.wang.beanmanager.message",
			"umoo.wang.beanmanager.persistence",
			"umoo.wang.beanmanager.cache" };

	public final static BeanFactory beanFactory = new InjectBeanFactory(
			new SingletonBeanFactory(), ROOT_PACKAGE_NAMES);
	private final static EventLoopGroup bossGroup = new NioEventLoopGroup(
			Runtime.getRuntime().availableProcessors() * 2);
	private final static EventLoopGroup workerGroup = new NioEventLoopGroup(
			100);

	@Inject
	private WebServerConfig config;
	@Inject
	private CoreRequestProcessor coreRequestProcessor;

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
						pipeline.addLast(coreRequestProcessor);
					}
				}).childOption(ChannelOption.SO_KEEPALIVE, true);

		try {
			ChannelFuture f = bootstrap.bind(config.getHost(),
					config.getPort());
			log.info("WebServer start...");
			f.sync();

			Client.start();
		} catch (InterruptedException e) {
			// 出错关闭bossGroup和workerGroup
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}
}
