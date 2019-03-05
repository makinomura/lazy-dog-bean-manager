package umoo.wang.beanmanager.client.socket;

import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import umoo.wang.beanmanager.message.reply.ReplyableCommand;
import umoo.wang.beanmanager.message.server.command.ServerHeartBeatCommand;
import umoo.wang.beanmanager.message.server.message.ServerHeartBeatMessage;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by yuanchen on 2019/01/14. 心跳任务
 */
public class HeartBeatTask {
	private final static Logger logger = LoggerFactory
			.getLogger(HeartBeatTask.class);

	private static ScheduledExecutorService es;

	private Channel channel;
	private long heartBeatIntervals;

	public HeartBeatTask(Channel channel, long heartBeatIntervals) {
		this.channel = channel;
		this.heartBeatIntervals = heartBeatIntervals;
		es = Executors.newScheduledThreadPool(1);
	}

	public void start() {
		logger.info("Heart-beat task start...");
		es.scheduleWithFixedDelay(() -> {
			ServerHeartBeatCommand cmd = new ServerHeartBeatCommand(
					new ServerHeartBeatMessage(System.currentTimeMillis()));
			channel.writeAndFlush(new ReplyableCommand<>(cmd,
					(Boolean success, Integer result) -> {
						if (success) {
							logger.info("Receive server response: " + result);
						} else {
							logger.info("Receive server response timeout");
						}
					}));
		}, 1000L, heartBeatIntervals, TimeUnit.MILLISECONDS);
	}

	public void shutdown() {

		if (!es.isShutdown() || !es.isTerminated()) {
			es.shutdownNow();

			logger.info("Heart-beat task shutdown!");
		}
	}
}
