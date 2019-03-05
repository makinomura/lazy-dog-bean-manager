package umoo.wang.beanmanager.common;

import java.io.Closeable;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created by yuanchen on 2019/02/22.
 */
public interface ResourceExecutor<R extends Closeable> {

	R getResource();

	void execute(Consumer<R> consumer);

	<T> T execute(Function<R, T> function);
}
