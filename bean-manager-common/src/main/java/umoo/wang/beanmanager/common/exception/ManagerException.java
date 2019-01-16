package umoo.wang.beanmanager.common.exception;

/**
 * Created by yuanchen on 2019/01/14. 框架异常
 */
public class ManagerException extends RuntimeException {

	public ManagerException() {
	}

	public ManagerException(String message) {
		super(message);
	}

	public ManagerException(String message, Throwable cause) {
		super(message, cause);
	}

	public ManagerException(Throwable cause) {
		super(cause);
	}

	public ManagerException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public static ManagerException wrap(Throwable throwable) {
		return new ManagerException(throwable);
	}
}
