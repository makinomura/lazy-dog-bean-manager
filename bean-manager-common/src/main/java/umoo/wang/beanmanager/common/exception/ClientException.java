package umoo.wang.beanmanager.common.exception;

/**
 * Created by yuanchen on 2019/01/14.
 */
public class ClientException extends ManagerException {
	public ClientException() {
	}

	public ClientException(String message) {
		super(message);
	}

	public ClientException(String message, Throwable cause) {
		super(message, cause);
	}

	public ClientException(Throwable cause) {
		super(cause);
	}

	public ClientException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public static ClientException wrap(Throwable throwable) {
		return new ClientException(throwable);
	}
}
