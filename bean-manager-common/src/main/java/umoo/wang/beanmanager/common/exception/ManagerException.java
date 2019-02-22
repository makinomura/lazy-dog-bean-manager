package umoo.wang.beanmanager.common.exception;

/**
 * Created by yuanchen on 2019/01/14. 框架异常
 */
public class ManagerException extends RuntimeException {
	private static Class<? extends ManagerException> exceptionRoot;

	static {
		exceptionRoot = determineExceptionRoot();
	}

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

	private static Class<? extends ManagerException> determineExceptionRoot() {
		try {
			Class.forName("umoo.wang.beanmanager.client.socket.Client");
			return ClientException.class;
		} catch (ClassNotFoundException ignored) {
		}

		try {
			Class.forName("umoo.wang.beanmanager.server.Server");
			return ServerException.class;
		} catch (ClassNotFoundException ignored) {
		}

		try {
			Class.forName("umoo.wang.beanmanager.web.WebServer");
			return WebServerException.class;
		} catch (ClassNotFoundException ignored) {
		}

		return ManagerException.class;
	}

	public static ManagerException wrap(Throwable throwable) {
		try {
			ManagerException exception = exceptionRoot.newInstance();
			exception.initCause(throwable);

			return exception;
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}

		return new ManagerException(throwable);
	}
}
