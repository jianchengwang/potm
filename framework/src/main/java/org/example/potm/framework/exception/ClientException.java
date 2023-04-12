package org.example.potm.framework.exception;

/**
 * @author jianchengwang
 * @date 2023/3/14
 */
public class ClientException extends CommonException {

	public ClientException(Enum<? extends ErrorCode> errorCode, Object... args) {
		super(errorCode, null, args);
	}

	public ClientException(String message, Enum<? extends ErrorCode> errorCode, Object... args) {
		super(message, errorCode, null, args);
	}

	public ClientException(Enum<? extends ErrorCode> errorCode, Throwable throwable, Object... args) {
		super(errorCode, throwable, args);
	}

	public ClientException(String message, Enum<? extends ErrorCode> errorCode, Throwable throwable, Object... args) {
		super(message, errorCode, throwable, args);
	}

}
