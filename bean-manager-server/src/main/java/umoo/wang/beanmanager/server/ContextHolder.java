package umoo.wang.beanmanager.server;

import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelHandlerContext;
import umoo.wang.beanmanager.message.FieldUpdateMessage;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yuanchen on 2019/01/11.
 */
public class ContextHolder {
	public static Map<String, ChannelHandlerContext> contexts = new HashMap<>();

	static {
		new Thread(() -> {
			for (;;) {
				FieldUpdateMessage msg = new FieldUpdateMessage("time",
						String.valueOf(System.currentTimeMillis()));

				contexts.values().forEach(ctx -> {
					try {
						ctx.writeAndFlush(JSON.toJSONString(msg));
					} catch (Exception e) {
						e.printStackTrace();
					}
				});

				System.out.println(
						"send update msg to " + contexts.size() + " client");

				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
}
