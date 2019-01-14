package umoo.wang.beanmanager.client.socket;

import io.netty.channel.Channel;
import umoo.wang.beanmanager.message.server.command.ServerHeartBeatCommand;
import umoo.wang.beanmanager.message.server.message.ServerHeartBeatMessage;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by yuanchen on 2019/01/14.
 */
public class HeartBeatTask {

	private static ExecutorService es;

	private Channel channel;
	private long heartBeatIntervals;

	public HeartBeatTask(Channel ctx, long heartBeatIntervals) {
		this.channel = ctx;
		this.heartBeatIntervals = heartBeatIntervals;

		es = Executors.newFixedThreadPool(1);
	}

	public void start() {

		es.submit(new Thread() {
			@Override
			public void run() {
				for (;;) {
					if (!isInterrupted()) {
						channel.writeAndFlush(new ServerHeartBeatCommand(
								new ServerHeartBeatMessage(
										System.currentTimeMillis())));
					}

					try {
						Thread.sleep(heartBeatIntervals);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});
	}

	public void shutdown() {
		es.shutdownNow();
	}
}
