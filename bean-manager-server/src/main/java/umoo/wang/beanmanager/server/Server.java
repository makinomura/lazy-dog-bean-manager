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
import umoo.wang.beanmanager.common.PropertyResolver;
import umoo.wang.beanmanager.common.beanfactory.BeanFactory;
import umoo.wang.beanmanager.common.beanfactory.SingletonBeanFactory;
import umoo.wang.beanmanager.message.CommandProcessor;
import umoo.wang.beanmanager.message.codec.CommandDecoder;
import umoo.wang.beanmanager.message.codec.CommandEncoder;
import umoo.wang.beanmanager.message.reply.ReplyInvoker;
import umoo.wang.beanmanager.message.reply.ReplyRegister;

/**
 * Created by yuanchen on 2019/01/11.
 */
public class Server {
	public final static BeanFactory beanFactory = new SingletonBeanFactory();

	static {
		buildBeans();
	}

	private static void buildBeans() {
		beanFactory.registerBean(CommandDecoder.class);
		beanFactory.registerBean(CommandEncoder.class);

		CommandProcessor commandProcessor = beanFactory
				.registerBean(ServerCommandProcessor.class);

		beanFactory.registerBean(MainInHandler.class, commandProcessor);

		ReplyRegister register = beanFactory.registerBean(ReplyRegister.class,
				5,
				5000L);
		beanFactory.registerBean(ReplyInvoker.class, register);

	}

	private final static EventLoopGroup bossGroup = new NioEventLoopGroup(
			Runtime.getRuntime().availableProcessors() * 2);
	private final static EventLoopGroup workerGroup = new NioEventLoopGroup(
			100);

	private final static Logger logger = LoggerFactory.getLogger(Server.class);

	public static void main(String[] args) {
		String host = PropertyResolver.read("lazydog.server.host");
		Integer port = PropertyResolver.read("lazydog.server.port",
				Integer.class);

		ServerBootstrap bootstrap = new ServerBootstrap();
		bootstrap.group(bossGroup, workerGroup)
				.channel(NioServerSocketChannel.class)
				.childHandler(new ChannelInitializer<SocketChannel>() {
					@Override
					protected void initChannel(SocketChannel channel)
							throws Exception {
						ChannelPipeline pipeline = channel.pipeline();
						pipeline.addLast(
								beanFactory.getBean(CommandDecoder.class));
						pipeline.addLast(
								beanFactory.getBean(ReplyInvoker.class));
						pipeline.addLast(
								beanFactory.getBean(CommandEncoder.class));
						pipeline.addLast(
								beanFactory.getBean(ReplyRegister.class));
						pipeline.addLast(
								beanFactory.getBean(MainInHandler.class));
					}
				}).childOption(ChannelOption.SO_KEEPALIVE, true);

		try {
			ChannelFuture f = bootstrap.bind(host, port);
			logger.info("Server start...");
			f.sync();
		} catch (InterruptedException e) {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}
}
