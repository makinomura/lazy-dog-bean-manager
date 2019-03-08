package umoo.wang.beanmanager.client.socket;

import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import umoo.wang.beanmanager.message.reply.ReplyableCommand;
import umoo.wang.beanmanager.message.server.command.HeartBeatCommand;
import umoo.wang.beanmanager.message.server.message.HeartBeatMessage;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by yuanchen on 2019/01/14. 心跳任务
 */
@Slf4j
public class HeartBeatTask {

	private static ScheduledExecutorService es;

	private Channel channel;
	private long heartBeatIntervals;

	public HeartBeatTask(Channel channel, long heartBeatIntervals) {
		this.channel = channel;
		this.heartBeatIntervals = heartBeatIntervals;
		es = Executors.newScheduledThreadPool(1);
	}

	public void start() {
		log.info("Heart-beat task start...");
		es.scheduleWithFixedDelay(() -> {
			HeartBeatCommand cmd = new HeartBeatCommand(
					new HeartBeatMessage(System.currentTimeMillis()));
			channel.writeAndFlush(new ReplyableCommand<>(cmd,
					(Boolean success, Integer result) -> {
						if (success) {
							log.info("Receive server response: " + result);
						} else {
							log.info("Receive server response timeout");
						}
					}));
		}, 1000L, heartBeatIntervals, TimeUnit.MILLISECONDS);
	}

	public void shutdown() {

		if (!es.isShutdown() || !es.isTerminated()) {
			es.shutdownNow();

			log.info("Heart-beat task shutdown!");
		}
	}
}
