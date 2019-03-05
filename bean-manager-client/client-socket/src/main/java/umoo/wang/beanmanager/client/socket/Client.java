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
import lombok.extern.slf4j.Slf4j;
import umoo.wang.beanmanager.common.beanfactory.Bean;
import umoo.wang.beanmanager.common.beanfactory.BeanFactory;
import umoo.wang.beanmanager.common.beanfactory.Inject;
import umoo.wang.beanmanager.common.beanfactory.InjectBeanFactory;
import umoo.wang.beanmanager.common.beanfactory.SingletonBeanFactory;
import umoo.wang.beanmanager.common.exception.ManagerException;
import umoo.wang.beanmanager.message.codec.CommandDecoder;
import umoo.wang.beanmanager.message.codec.CommandEncoder;
import umoo.wang.beanmanager.message.reply.ReplyInvoker;
import umoo.wang.beanmanager.message.reply.ReplyRegister;
import umoo.wang.beanmanager.message.server.command.RegisterCommand;
import umoo.wang.beanmanager.message.server.message.RegisterMessage;

import java.util.concurrent.TimeUnit;

/**
 * Created by yuanchen on 2019/01/11. Client通讯类
 */
@Slf4j
@Bean
public class Client {
	private final static String ROOT_PACKAGE_NAME = "umoo.wang.beanmanager";
	// client的对象工厂
	public final static BeanFactory beanFactory = new InjectBeanFactory(
			new SingletonBeanFactory(), ROOT_PACKAGE_NAME);

	private HeartBeatTask heartBeatTask;
	private ChannelFuture channelFuture;

	@Inject
	private ClientConfig config;
	@Inject
	private CommandDecoder commandDecoder;
	@Inject
	private CommandEncoder commandEncoder;
	@Inject
	private ReplyInvoker replyInvoker;
	@Inject
	private ReplyRegister replyRegister;
	@Inject
	private ClientCommandProcessor clientCommandProcessor;

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
						pipeline.addLast(commandDecoder);
						pipeline.addLast(replyInvoker);
						pipeline.addLast(commandEncoder);
						pipeline.addLast(replyRegister);
						pipeline.addLast(clientCommandProcessor);
					}
				});

		try {
			channelFuture = bootstrap.connect(config.getHost(),
					config.getPort());
			channelFuture.addListener((ChannelFutureListener) future -> {
				if (future.isSuccess()) {
					log.info("Server connect successful!");

					// 连接Server成功开始心跳任务
					heartBeatTask = new HeartBeatTask(channelFuture.channel(),
							60000L);

					heartBeatTask.start();

					doRegister(future.channel());
				} else {
					log.warn(
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
			throw ManagerException.wrap(e);
		}
	}

	private void doRegister(Channel channel) {
		channel.eventLoop().schedule(() -> {
			channel.writeAndFlush(
					new RegisterCommand(new RegisterMessage(
							config.getAppName(), config.getEnvironmentName())));
		}, 1000L, TimeUnit.MILLISECONDS);
	}

	@Override
	public String toString() {
		return "Client{" + "config=" + config + '}';
	}
}
