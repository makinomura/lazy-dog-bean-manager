package umoo.wang.beanmanager.common.exception;

/**
 * Created by yuanchen on 2019/01/14.
 */
public class RetryException extends RuntimeException {

	public RetryException() {
	}

	public RetryException(String message) {
		super(message);
	}

	public RetryException(String message, Throwable cause) {
		super(message, cause);
	}

	public RetryException(Throwable cause) {
		super(cause);
	}

	public RetryException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
