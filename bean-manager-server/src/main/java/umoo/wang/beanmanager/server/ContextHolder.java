package umoo.wang.beanmanager.server;

import io.netty.channel.ChannelHandlerContext;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yuanchen on 2019/01/11. 保存Client的Channel实体，以便发送消息
 */
public class ContextHolder {
	public static Map<String, ChannelHandlerContext> contexts = new HashMap<>();
}
