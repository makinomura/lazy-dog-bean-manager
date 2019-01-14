package umoo.wang.beanmanager.server;

import io.netty.channel.ChannelHandlerContext;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yuanchen on 2019/01/11.
 */
public class ContextHolder {
	public static Map<String, ChannelHandlerContext> contexts = new HashMap<>();
}
