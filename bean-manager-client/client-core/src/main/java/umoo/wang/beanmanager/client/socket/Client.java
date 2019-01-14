package umoo.wang.beanmanager.client.socket;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import umoo.wang.beanmanager.common.Retryer;
import umoo.wang.beanmanager.common.exception.ClientException;
import umoo.wang.beanmanager.message.codec.CommandDecoder;
import umoo.wang.beanmanager.message.codec.CommandEncoder;

/**
 * Created by yuanchen on 2019/01/11.
 */
public class Client {

	private static final Logger logger = LoggerFactory.getLogger(Client.class);
	private String host;
	private int port;

	public Client(String host, int port) {
		this.host = host;
		this.port = port;
	}

	public static void start(String host, int port) {
		Retryer.build(3, () -> {
			Client client = new Client(host, port);
			client.init();

			return client;
		}).intervals(50).retryIfException(e -> {
			logger.error("Connect to server failed, retry after 5000ms.", e);
			return true;
		}).run((success, result) -> {
			if (!success) {
				logger.error("Client connect failed!");
			} else {
				logger.info("Connect success at {}", result.toString());
			}
		});
	}

	public void init() {
		Bootstrap bootstrap = new Bootstrap();
		EventLoopGroup workerGroup = new NioEventLoopGroup(100);
		bootstrap.group(workerGroup).channel(NioSocketChannel.class)
				.handler(new ChannelInitializer<SocketChannel>() {
					@Override
					protected void initChannel(SocketChannel channel)
							throws Exception {
						ChannelPipeline pipeline = channel.pipeline();
						pipeline.addLast(new CommandDecoder());
						pipeline.addLast(new CommandEncoder());
						pipeline.addLast(new MainInHandler());
					}
				});
		HeartBeatTask heartBeatTask = null;
		try {
			ChannelFuture f = bootstrap.connect(host, port).sync();

			heartBeatTask = new HeartBeatTask(f.channel(), 5000);
			heartBeatTask.start();
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
