package umoo.wang.beanmanager.server;

import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

/**
 * Created by yuanchen on 2019/01/11.
 */
public class MainOutHandler extends ChannelOutboundHandlerAdapter {

	@Override
	public void write(ChannelHandlerContext ctx, Object msg,
			ChannelPromise promise) throws Exception {
		try {
            byte[] bytes = JSON.toJSONBytes(msg);
            ByteBuf byteBuf = ctx.alloc().buffer(bytes.length).writeBytes(bytes);
            ctx.write(byteBuf, promise);
        } catch (Exception e) {
		    e.printStackTrace();
        }
	}
}
