package umoo.wang.beanmanager.message.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import umoo.wang.beanmanager.message.Command;
import umoo.wang.beanmanager.message.serializer.CommandSerializer;

import java.util.List;

/**
 * Created by yuanchen on 2019/01/11.
 */
public class CommandEncoder extends MessageToMessageEncoder<Command> {

	private CommandSerializer serializer = CommandSerializer.load();

	@Override
	protected void encode(ChannelHandlerContext channelHandlerContext,
			Command command, List<Object> list) throws Exception {
		byte[] bytes = serializer.serialize(command);
		ByteBuf byteBuf = channelHandlerContext.alloc().buffer(bytes.length)
				.writeBytes(bytes);

		list.add(byteBuf);
	}
}
