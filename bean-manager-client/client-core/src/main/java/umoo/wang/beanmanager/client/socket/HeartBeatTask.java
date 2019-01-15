package umoo.wang.beanmanager.client.socket;

import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import umoo.wang.beanmanager.message.reply.ReplyableCommand;
import umoo.wang.beanmanager.message.server.command.ServerHeartBeatCommand;
import umoo.wang.beanmanager.message.server.message.ServerHeartBeatMessage;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by yuanchen on 2019/01/14.
 */
public class HeartBeatTask {
	private final static Logger logger = LoggerFactory
			.getLogger(HeartBeatTask.class);

	private static ExecutorService es;

	private Channel channel;
	private long heartBeatIntervals;

	public HeartBeatTask(Channel channel, long heartBeatIntervals) {
		this.channel = channel;
		this.heartBeatIntervals = heartBeatIntervals;
		es = Executors.newFixedThreadPool(1);
	}

	public void start() {
		logger.info("Heart-beat task start...");

		es.submit(new Thread() {
			@Override
			public void run() {
				for (;;) {
					if (!isInterrupted()) {
						ServerHeartBeatCommand cmd = new ServerHeartBeatCommand(
								new ServerHeartBeatMessage(
										System.currentTimeMillis()));
						channel.writeAndFlush(new ReplyableCommand<>(cmd,
								(Boolean success, Integer result) -> {
									logger.info("Receive server response: "
											+ result);
								}));
					} else {
						break;
					}

					try {
						Thread.sleep(heartBeatIntervals);
					} catch (InterruptedException e) {
						break;
					}
				}
			}
		});
	}

	public void shutdown() {
		if (!es.isShutdown() || !es.isTerminated()) {
			es.shutdownNow();

			logger.info("Heart-beat task shutdown!");
		}
	}
}
