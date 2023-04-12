package org.example.potm.framework.exception;

/**
 * @author jianchengwang
 * @date 2023/3/14
 */
public class ServerException extends CommonException {

	public ServerException(Enum<? extends ErrorCode> errorCode, Object... args) {
		super(errorCode, null, args);
	}

	public ServerException(Enum<? extends ErrorCode> errorCode, Throwable throwable, Object... args) {
		super(errorCode, throwable, args);
	}

	public ServerException(String message, Enum<? extends ErrorCode> errorCode, Throwable throwable, Object... args) {
		super(message, errorCode, throwable, args);
	}

}
