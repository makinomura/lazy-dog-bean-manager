package umoo.wang.beanmanager.client.socket;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
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
import umoo.wang.beanmanager.common.beanfactory.Inject;
import umoo.wang.beanmanager.common.beanfactory.InjectBeanFactory;
import umoo.wang.beanmanager.common.beanfactory.SingletonBeanFactory;
import umoo.wang.beanmanager.common.exception.ClientException;
import umoo.wang.beanmanager.message.codec.CommandDecoder;
import umoo.wang.beanmanager.message.codec.CommandEncoder;
import umoo.wang.beanmanager.message.reply.ReplyInvoker;
import umoo.wang.beanmanager.message.reply.ReplyRegister;
import umoo.wang.beanmanager.message.server.command.ServerRegisterCommand;
import umoo.wang.beanmanager.message.server.message.ServerRegisterMessage;

/**
 * Created by yuanchen on 2019/01/11. Client通讯类
 */
public class Client {

	// client的对象工厂
	public final static InjectBeanFactory beanFactory = new InjectBeanFactory(
			new SingletonBeanFactory());
	private final static Logger logger = LoggerFactory.getLogger(Client.class);

	static {
		buildBeans();
	}

	private HeartBeatTask heartBeatTask;
	private ChannelFuture channelFuture;

	@Inject
	private ClientConfig config;

	/**
	 * 构建beans
	 */
	private static void buildBeans() {
		// Command解码
		beanFactory.createBean(CommandDecoder.class);
		// Command编码
		beanFactory.createBean(CommandEncoder.class);
		// Command消息处理器
		beanFactory.createBean(ClientCommandProcessor.class);
		// 消息入口
		beanFactory.createBean(MainInHandler.class);
		// Replyable消息注册
		beanFactory.createBean(ReplyRegister.class, 5, 50000L);
		// Replyable消息回调
		beanFactory.createBean(ReplyInvoker.class);

		beanFactory.createBean(ClientConfig.class);
		beanFactory.createBean(Client.class);

		beanFactory.doInject();
	}

	public static void start() {
		beanFactory.getBean(Client.class).connect();
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
			channelFuture = bootstrap.connect(config.getHost(),
					config.getPort());
			channelFuture.addListener((ChannelFutureListener) future -> {
				if (future.isSuccess()) {
					logger.info("Server connect successful!");

					// 连接Server成功开始心跳任务
					heartBeatTask = beanFactory.createBean(HeartBeatTask.class,
							channelFuture.channel(), 60000L);

					heartBeatTask.start();

					doRegister(future.channel());
				} else {
					logger.warn(
							"Server connect failed, schedule to connect again...");

					// TODO 确定schedule方法不执行的原因，暂用Thread.sleep
					future.channel().eventLoop().submit(() -> {

						// 5秒后重新连接
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

			// 出错关闭心跳任务和workerGroup
			if (heartBeatTask != null) {
				heartBeatTask.shutdown();
			}
			workerGroup.shutdownGracefully();
			throw ClientException.wrap(e);
		}
	}

	private void doRegister(Channel channel) {
		channel.writeAndFlush(
				new ServerRegisterCommand(new ServerRegisterMessage(
						config.getAppName(), config.getEnvironmentName())));
	}

	@Override
	public String toString() {
		return "Client{" + "config=" + config + '}';
	}
}
