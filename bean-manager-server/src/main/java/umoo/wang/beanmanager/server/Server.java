package umoo.wang.beanmanager.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import umoo.wang.beanmanager.common.beanfactory.Bean;
import umoo.wang.beanmanager.common.beanfactory.BeanFactory;
import umoo.wang.beanmanager.common.beanfactory.Inject;
import umoo.wang.beanmanager.common.beanfactory.InjectBeanFactory;
import umoo.wang.beanmanager.common.beanfactory.SingletonBeanFactory;
import umoo.wang.beanmanager.message.codec.CommandDecoder;
import umoo.wang.beanmanager.message.codec.CommandEncoder;
import umoo.wang.beanmanager.message.reply.ReplyInvoker;
import umoo.wang.beanmanager.message.reply.ReplyRegister;

/**
 * Created by yuanchen on 2019/01/11. Server负责与Client通讯
 */
@Bean
public class Server {
	private final static String ROOT_PACKAGE_NAME = "umoo.wang.beanmanager";
	public final static BeanFactory beanFactory = new InjectBeanFactory(
			new SingletonBeanFactory(), ROOT_PACKAGE_NAME);
	private final static EventLoopGroup bossGroup = new NioEventLoopGroup(
			Runtime.getRuntime().availableProcessors() * 2);
	private final static EventLoopGroup workerGroup = new NioEventLoopGroup(
			100);
	private final static Logger logger = LoggerFactory.getLogger(Server.class);

	@Inject
	private ServerConfig config;
	@Inject
	private CommandDecoder commandDecoder;
	@Inject
	private CommandEncoder commandEncoder;
	@Inject
	private ReplyInvoker replyInvoker;
	@Inject
	private ReplyRegister replyRegister;
	@Inject
	private ServerCommandProcessor serverCommandProcessor;

	public static void main(String[] args) {
		beanFactory.getBean(Server.class).run();
	}

	private void run() {
		ServerBootstrap bootstrap = new ServerBootstrap();
		bootstrap.group(bossGroup, workerGroup)
				.channel(NioServerSocketChannel.class)
				.childHandler(new ChannelInitializer<SocketChannel>() {
					@Override
					protected void initChannel(SocketChannel channel)
							throws Exception {
						ChannelPipeline pipeline = channel.pipeline();
						pipeline.addLast(commandDecoder);
						pipeline.addLast(replyInvoker);
						pipeline.addLast(commandEncoder);
						pipeline.addLast(replyRegister);
						pipeline.addLast(serverCommandProcessor);
					}
				}).childOption(ChannelOption.SO_KEEPALIVE, true);

		try {
			ChannelFuture f = bootstrap.bind(config.getHost(),
					config.getPort());
			logger.info("Server start...");
			f.sync();
		} catch (InterruptedException e) {
			// 出错关闭bossGroup和workerGroup
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}
}
