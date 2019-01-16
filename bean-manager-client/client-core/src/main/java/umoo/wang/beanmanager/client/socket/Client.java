package umoo.wang.beanmanager.client.socket;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import umoo.wang.beanmanager.common.beanfactory.BeanFactory;
import umoo.wang.beanmanager.common.beanfactory.SingletonBeanFactory;
import umoo.wang.beanmanager.common.exception.ClientException;
import umoo.wang.beanmanager.message.CommandProcessor;
import umoo.wang.beanmanager.message.codec.CommandDecoder;
import umoo.wang.beanmanager.message.codec.CommandEncoder;
import umoo.wang.beanmanager.message.reply.ReplyInvoker;
import umoo.wang.beanmanager.message.reply.ReplyRegister;

/**
 * Created by yuanchen on 2019/01/11.
 */
public class Client {
	public final static BeanFactory beanFactory = new SingletonBeanFactory();
	private final static Logger logger = LoggerFactory.getLogger(Client.class);

	static {
		buildBeans();
	}

	private HeartBeatTask heartBeatTask;
	private ChannelFuture channelFuture;
	private String host;
	private Integer port;

	public Client(String host, Integer port) {
		this.host = host;
		this.port = port;
	}

	private static void buildBeans() {
		beanFactory.registerBean(CommandDecoder.class);
		beanFactory.registerBean(CommandEncoder.class);
		CommandProcessor commandProcessor = beanFactory
				.registerBean(ClientCommandProcessor.class);

		beanFactory.registerBean(MainInHandler.class, commandProcessor);
		ReplyRegister register = beanFactory.registerBean(ReplyRegister.class,
				5,
				50000L);
		beanFactory.registerBean(ReplyInvoker.class, register);
	}

	public static void start(String host, Integer port) {
		beanFactory.registerBean(Client.class, host, port).connect();
	}

	public void connect() {
		Bootstrap bootstrap = new Bootstrap();
		EventLoopGroup workerGroup = new NioEventLoopGroup(100);
		bootstrap.group(workerGroup).channel(NioSocketChannel.class)
				.handler(new ChannelInitializer<SocketChannel>() {
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
				});

		try {
			channelFuture = bootstrap.connect(host, port);
			channelFuture.addListener((ChannelFutureListener) future -> {
				if (future.isSuccess()) {
					logger.info("Server connect successful!");

					heartBeatTask = beanFactory.registerBean(
							HeartBeatTask.class,
							channelFuture.channel(), 5000L);
					;
					heartBeatTask.start();
				} else {
					logger.warn(
							"Server connect failed, schedule to connect again...");

					future.channel().eventLoop().submit(() -> {
						try {
							Thread.sleep(5000L);
						} catch (InterruptedException e) {
						}

						beanFactory.getBean(Client.class).connect();
					});
				}
			});

			channelFuture.sync();
		} catch (Exception e) {
			if (heartBeatTask != null) {
				heartBeatTask.shutdown();
			}
			workerGroup.shutdownGracefully();
			throw ClientException.wrap(e);
		}
	}

	@Override
	public String toString() {
		return "Client{" + "host='" + host + '\'' + ", port=" + port + '}';
	}
}
