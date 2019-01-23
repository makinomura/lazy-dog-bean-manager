package umoo.wang.beanmanager.server;

import io.netty.channel.ChannelHandlerContext;
import lombok.Data;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by yuanchen on 2019/01/11. 保存Client的Channel实体，以便发送消息
 */
public class ClientManager {
	private static Map<String, ContextWrapper> contexts = new ConcurrentHashMap<>();

	/**
	 * 构建key ip:port
	 *
	 * @param ctx
	 * @return
	 */
	private static String buildContextKey(ChannelHandlerContext ctx) {
		InetSocketAddress inetSocketAddress = (InetSocketAddress) ctx.channel()
				.remoteAddress();

		return inetSocketAddress.getHostString() + ":"
				+ inetSocketAddress.getPort();
	}

	public static void register(ChannelHandlerContext ctx) {
		String name = buildContextKey(ctx);

		ContextWrapper contextWrapper = new ContextWrapper(name, ctx,
				ClientStatusEnum.CONNECTED.value());
		contexts.put(name, contextWrapper);
	}

	public static void unregister(ChannelHandlerContext ctx) {
		contexts.remove(buildContextKey(ctx));
	}

	public static void updateStatus(ChannelHandlerContext ctx, int status) {

		ContextWrapper context = getOrRegister(ctx);
		context.setClientStatus(status);

	}

	public static ClientConfig getConfig(ChannelHandlerContext ctx) {
		return getOrRegister(ctx).clientConfig;
	}

	private static ContextWrapper getOrRegister(ChannelHandlerContext ctx) {
		String name = buildContextKey(ctx);

		ContextWrapper contextWrapper = contexts.get(name);
		if (contextWrapper == null) {
			register(ctx);
			contextWrapper = contexts.get(name);
		}

		return contextWrapper;
	}

	public static class ContextWrapper {
		private String name;
		private ChannelHandlerContext ctx;
		private int clientStatus;
		private ClientConfig clientConfig;

		public ContextWrapper(String name, ChannelHandlerContext ctx,
				int clientStatus) {
			this.name = name;
			this.ctx = ctx;
			this.clientStatus = clientStatus;
			this.clientConfig = new ClientConfig();
		}

		public ChannelHandlerContext getCtx() {
			return ctx;
		}

		public String getName() {
			return name;
		}

		public int getClientStatus() {
			return clientStatus;
		}

		public void setClientStatus(int clientStatus) {
			this.clientStatus = clientStatus;
		}

		public ClientConfig getClientConfig() {
			return clientConfig;
		}
	}

	@Data
	public static class ClientConfig {
		private String appName;
		private String environment;
	}

}
