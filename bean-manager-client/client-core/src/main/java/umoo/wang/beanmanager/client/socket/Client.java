package umoo.wang.beanmanager.client.socket;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

/**
 * Created by yuanchen on 2019/01/11.
 */
public class Client {
	private static final EventLoopGroup workerGroup = new NioEventLoopGroup(
			100);

	public static void init() {
		Bootstrap bootstrap = new Bootstrap();
		bootstrap.group(workerGroup).channel(NioSocketChannel.class)
				.handler(new ChannelInitializer<SocketChannel>() {
					@Override
					protected void initChannel(SocketChannel channel)
							throws Exception {
						ChannelPipeline pipeline = channel.pipeline();
						pipeline.addLast(new StringDecoder());

						pipeline.addLast(new StringEncoder(CharsetUtil.UTF_8));
						pipeline.addLast(new MainHandler());
					}
				}).option(ChannelOption.SO_BACKLOG, 128);

		try {
			ChannelFuture f = bootstrap.connect("localhost", 9999).sync();
		} catch (InterruptedException e) {
			workerGroup.shutdownGracefully();
		}
	}
}
