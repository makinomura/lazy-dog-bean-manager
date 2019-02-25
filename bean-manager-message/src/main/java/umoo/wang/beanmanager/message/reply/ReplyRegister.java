package umoo.wang.beanmanager.message.reply;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import umoo.wang.beanmanager.common.beanfactory.Bean;
import umoo.wang.beanmanager.common.beanfactory.Conf;
import umoo.wang.beanmanager.common.beanfactory.PostConstruct;
import umoo.wang.beanmanager.message.Command;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by yuanchen on 2019/01/15. Replyable消息注册
 */
@Bean
@ChannelHandler.Sharable
@SuppressWarnings("unchecked")
public class ReplyRegister extends ChannelOutboundHandlerAdapter {

	private final static int CLEAN_TASK_INTERVALS = 5000;

	@Conf(key = "lazydog.message.ReplyRegister.replyExpireTime", defaultValue = "5000")
	private Long replyExpireTime;// 消息过期时间
	private ScheduledExecutorService executor;
	private Map<String, ReplyableCommand<Boolean, Object>> commandMap;

	@PostConstruct
	public void init() {
		commandMap = new ConcurrentHashMap<>();

		// 定时清理超时未得到响应的消息
		executor = Executors.newScheduledThreadPool(
				Runtime.getRuntime().availableProcessors() * 2 + 1);
		startCleanExpireReplyTask();
	}

	@Override
	public void write(ChannelHandlerContext ctx, Object msg,
			ChannelPromise promise) throws Exception {

		if (msg instanceof ReplyableCommand) {
			ReplyableCommand replyableCommand = (ReplyableCommand) msg;
			commandMap.put(replyableCommand.getCommand().getCommandId(),
					replyableCommand);
			msg = replyableCommand.getCommand();
		}

		ctx.writeAndFlush(msg, promise);
	}

	/**
	 * 调用回调
	 * 
	 * @param command
	 */
	public void invokeCallback(Command command) {
		commandMap.computeIfPresent(command.getReplyTo(), (k, v) -> {
			executor.submit(() -> {
				v.getCallback().accept(true, command.getCommandObj());
			});
			return null;
		});
	}

	/**
	 * 移除过期消息
	 */
	private void startCleanExpireReplyTask() {
		executor.scheduleWithFixedDelay(() -> {
			long now = System.currentTimeMillis();

			commandMap.entrySet().removeIf(entry -> {
				ReplyableCommand<Boolean, Object> command = entry.getValue();
				if (now - command.getCommand()
						.getTimestamps() > replyExpireTime) {
					executor.submit(() -> {
						command.getCallback().accept(false, null);
					});

					return true;
				}
				return false;
			});
		}, 0, CLEAN_TASK_INTERVALS, TimeUnit.MILLISECONDS);
	}
}
