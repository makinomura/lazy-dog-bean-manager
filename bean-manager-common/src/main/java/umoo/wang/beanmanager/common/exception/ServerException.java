package umoo.wang.beanmanager.common.exception;

/**
 * Created by yuanchen on 2019/01/14. Server异常
 */
public class ServerException extends ManagerException {
	public ServerException() {
	}

	public ServerException(String message) {
		super(message);
	}

	public ServerException(String message, Throwable cause) {
		super(message, cause);
	}

	public ServerException(Throwable cause) {
		super(cause);
	}

	public ServerException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
