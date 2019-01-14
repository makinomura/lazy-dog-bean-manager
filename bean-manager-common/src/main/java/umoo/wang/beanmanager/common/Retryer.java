package umoo.wang.beanmanager.common;

import umoo.wang.beanmanager.common.exception.RetryException;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

/**
 * Created by yuanchen on 2019/01/14.
 */
public class Retryer<V> {
	private BiConsumer<Boolean, V> emptyConsumer = (success, result) -> {
	};

	private int count;
	private long intervals = -1;
	private long maxTime = -1;

	private Callable<V> task;
	private Predicate<Exception> retryIfException = e -> false;

	public Retryer(int count, Callable<V> task) {
		this.count = count;
		this.task = task;
	}

	public static <V> Retryer<V> build(int count, Callable<V> task) {
		return new Retryer<>(count, task);
	}

	public Retryer<V> intervals(long intervals) {
		this.intervals = intervals;

		return this;
	}

	public Retryer<V> maxTime(long maxTime) {
		this.maxTime = maxTime;
		return this;
	}

	public Retryer<V> retryIfException(Predicate<Exception> retryIfException) {
		this.retryIfException = retryIfException;
		return this;
	}

	public void run() {
		run(emptyConsumer);
	}

	public void run(BiConsumer<Boolean, V> resultConsumer) {
		ExecutorService executors = new ThreadPoolExecutor(1,
				Runtime.getRuntime().availableProcessors() * 2 + 1, 0L,
				TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());

		ExecutorService watcher = new ThreadPoolExecutor(1,
				Runtime.getRuntime().availableProcessors() * 2 + 1, 0L,
				TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
		Object[] resultWrapper = { null, null, null };

		for (int i = 0; i < count; i++) {
			try {
				Object sync = new Object();

				Object[] finalResultWrapper = resultWrapper;
				executors.submit(() -> {
					try {
						finalResultWrapper[0] = task.call();
						finalResultWrapper[1] = true;
					} catch (Exception e) {
						finalResultWrapper[1] = false;
						finalResultWrapper[2] = e;
					} finally {
						synchronized (sync) {
							sync.notifyAll();
						}
					}
				});

				if (maxTime > 0) {
					watcher.submit(() -> {
						try {
							Thread.sleep(maxTime);
							finalResultWrapper[2] = new RetryException(
									"operation not finished in " + maxTime
											+ " ms.");
						} catch (InterruptedException e) {
							finalResultWrapper[2] = new RetryException(e);
						} finally {
							synchronized (sync) {
								sync.notifyAll();
							}
						}
					});
				}

				synchronized (sync) {
					if (resultWrapper[1] == null) {
						sync.wait();
					}
				}
				if (resultWrapper[1] != null && ((Boolean) resultWrapper[1])) {
					break;
				} else if (resultWrapper[2] != null && retryIfException
						.test(((Exception) resultWrapper[2]))) {
					if (intervals > 0) {
						Thread.sleep(intervals);
					}
					resultWrapper = new Object[] { null, null, null };
				} else {
					break;
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		executors.shutdown();
		if (resultWrapper[1] == null) {
			resultWrapper[1] = false;
		}

		resultConsumer.accept((Boolean) resultWrapper[1],
				((V) resultWrapper[0]));
	}
}
