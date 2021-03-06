package umoo.wang.beanmanager.message.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import umoo.wang.beanmanager.common.beanfactory.Bean;
import umoo.wang.beanmanager.message.serializer.CommandSerializer;

import java.util.List;

/**
 * Created by yuanchen on 2019/01/14. Command消息解码器
 */
@Bean
@ChannelHandler.Sharable
public class CommandDecoder extends MessageToMessageDecoder<ByteBuf> {

	private CommandSerializer serializer = CommandSerializer.load();

	@Override
	protected void decode(ChannelHandlerContext channelHandlerContext,
			ByteBuf byteBuf, List<Object> list) throws Exception {

		byte[] bytes = ByteBufUtil.getBytes(byteBuf);
		list.addAll(serializer.deserialize(bytes));
	}
}
