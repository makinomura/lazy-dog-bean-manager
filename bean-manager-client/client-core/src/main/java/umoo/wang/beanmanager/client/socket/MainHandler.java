package umoo.wang.beanmanager.client.socket;

import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import umoo.wang.beanmanager.client.BeanManager;
import umoo.wang.beanmanager.message.FieldUpdateMessage;

/**
 * Created by yuanchen on 2019/01/11.
 */
public class MainHandler extends SimpleChannelInboundHandler<String> {

	@Override
	protected void channelRead0(ChannelHandlerContext channelHandlerContext,
			String o) throws Exception {
		FieldUpdateMessage message = JSON.parseObject(o,
				FieldUpdateMessage.class);
		BeanManager.update(message.getFieldName(), message.getNewValue());

	}
}
