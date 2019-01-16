package umoo.wang.beanmanager.message.reply;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import umoo.wang.beanmanager.message.Command;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by yuanchen on 2019/01/15.
 */
@ChannelHandler.Sharable
@SuppressWarnings("unchecked")
public class ReplyRegister extends ChannelOutboundHandlerAdapter {

	private final static int CLEAN_TASK_INTERVALS = 5000;

	private Long replyExpireTime;
	private ScheduledExecutorService executor;
	private Map<String, ReplyableCommand<Boolean, Object>> commandMap;

	public ReplyRegister(Integer maxThreads, Long replyExpireTime) {
		this.replyExpireTime = replyExpireTime;
		commandMap = new ConcurrentHashMap<>();

		executor = Executors.newScheduledThreadPool(maxThreads);
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

	public void invokeCallback(Command command) {
		commandMap.computeIfPresent(command.getReplyTo(), (k, v) -> {
			executor.submit(() -> {
				v.getCallback().accept(true, command.getCommandObj());
			});
			return null;
		});
	}

	private void startCleanExpireReplyTask() {
		executor.scheduleWithFixedDelay(() -> {
			long now = System.currentTimeMillis();
			Set<String> expireKeys = new HashSet<>();

			commandMap.forEach((commandId, command) -> {
				if (now - command.getCommand()
						.getTimestamps() > replyExpireTime) {
					expireKeys.add(commandId);
				}
			});

			expireKeys.forEach(commandMap::remove);
		}, 0, CLEAN_TASK_INTERVALS, TimeUnit.MILLISECONDS);
	}
}
