package umoo.wang.beanmanager.common.exception;

/**
 * Created by yuanchen on 2019/01/14. WebServer异常
 */
public class WebServerException extends ManagerException {
	public WebServerException() {
	}

	public WebServerException(String message) {
		super(message);
	}

	public WebServerException(String message, Throwable cause) {
		super(message, cause);
	}

	public WebServerException(Throwable cause) {
		super(cause);
	}

	public WebServerException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
