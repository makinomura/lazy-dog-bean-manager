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
import umoo.wang.beanmanager.common.beanfactory.InjectBeanFactory;
import umoo.wang.beanmanager.common.beanfactory.SingletonBeanFactory;
import umoo.wang.beanmanager.message.codec.CommandDecoder;
import umoo.wang.beanmanager.message.codec.CommandEncoder;
import umoo.wang.beanmanager.message.reply.ReplyInvoker;
import umoo.wang.beanmanager.message.reply.ReplyRegister;
import umoo.wang.beanmanager.persistence.SqlSessionManager;
import umoo.wang.beanmanager.persistence.generated.mapper.VersionMapper;
import umoo.wang.beanmanager.server.persistence.entity.Version;
import umoo.wang.beanmanager.server.processor.AckProcessor;
import umoo.wang.beanmanager.server.processor.ServerHeartBeatCommandProcessor;
import umoo.wang.beanmanager.server.processor.ServerRegisterCommandProcessor;

import java.util.Date;

/**
 * Created by yuanchen on 2019/01/11. Server负责与Client通讯
 */
public class Server {
	public final static InjectBeanFactory beanFactory = new InjectBeanFactory(
			new SingletonBeanFactory());
	private final static EventLoopGroup bossGroup = new NioEventLoopGroup(
			Runtime.getRuntime().availableProcessors() * 2);
	private final static EventLoopGroup workerGroup = new NioEventLoopGroup(
			100);
	private final static Logger logger = LoggerFactory.getLogger(Server.class);

	static {
		buildBeans();
	}

	/**
	 * 构建Beans
	 */
	private static void buildBeans() {
		// Command解码
		beanFactory.createBean(CommandDecoder.class);
		// Command编码
		beanFactory.createBean(CommandEncoder.class);
		// Command消息处理器
		beanFactory.createBean(ServerCommandProcessor.class);
		beanFactory.createBean(AckProcessor.class);
		beanFactory.createBean(ServerHeartBeatCommandProcessor.class);
		beanFactory.createBean(ServerRegisterCommandProcessor.class);
		// 消息入口
		beanFactory.createBean(MainInHandler.class);
		// Replyable消息注册
		beanFactory.createBean(ReplyRegister.class, 5, 5000L);
		// Replyable消息回调
		beanFactory.createBean(ReplyInvoker.class);

		beanFactory.doInject();
	}

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
			// 出错关闭bossGroup和workerGroup
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}
}
