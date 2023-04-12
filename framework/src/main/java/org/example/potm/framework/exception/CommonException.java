package org.example.potm.framework.exception;

import lombok.Getter;

/**
 * @author jianchengwang
 * @date 2023/3/14
 */
@Getter
public class CommonException extends RuntimeException {

	private final static String UNKNOWN_CODE = "UNKNOWN";

	/**
	 * 异常类型
	 */
	private final ErrorCode errorCode;

	/**
	 * 调用参数
	 */
	private Object[] args;

	public CommonException(Enum<? extends ErrorCode> errorCode, Throwable throwable, Object... args) {
		this(getCodeMessage((ErrorCode) errorCode), errorCode, throwable, args);
	}

	public CommonException(String message, Enum<? extends ErrorCode> errorCode, Throwable throwable, Object... args) {
		super(message, throwable);
		this.errorCode = (ErrorCode) errorCode;
		this.args = args;
	}

	/**
	 * 获取异常状态码
	 * @return 不存在时返回<code>UNKNOWN</code>
	 */
	public String getCode() {
		return getCode(errorCode);
	}

	/**
	 * 获取异常状态码，简单码不包含模块
	 * @return 不存在时返回<code>UNKNOWN</code>
	 */
	public String getSimpleCode() {
		return errorCode == null ? UNKNOWN_CODE : errorCode.toString();
	}

	/**
	 * 获取异常状态码说明
	 * @return 不存在时返回<code>UNKNOWN</code>
	 */
	public String getCodeMessage() {
		return errorCode == null ? UNKNOWN_CODE : errorCode.getMessage();
	}

	private static String getCodeMessage(ErrorCode errorInfo) {
		return errorInfo == null ? UNKNOWN_CODE : (errorInfo.getModule() + ":" + errorInfo.getMessage());
	}

	private static String getCode(ErrorCode errorInfo) {
		return errorInfo == null ? UNKNOWN_CODE : (errorInfo.getModule() + ":" + errorInfo.toString());
	}

}
