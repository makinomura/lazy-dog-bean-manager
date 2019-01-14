package umoo.wang.beanmanager.client.socket;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import umoo.wang.beanmanager.message.codec.CommandDecoder;
import umoo.wang.beanmanager.message.codec.CommandEncoder;

/**
 * Created by yuanchen on 2019/01/11.
 */
public class Client {
	private static final EventLoopGroup workerGroup = new NioEventLoopGroup(
			100);

	public static void init(String host, int port) {
		Bootstrap bootstrap = new Bootstrap();
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
		}
	}
}
