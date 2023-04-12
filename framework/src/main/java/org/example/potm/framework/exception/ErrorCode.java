package org.example.potm.framework.exception;

/**
 * @author jianchengwang
 * @date 2023/3/14
 */
public interface ErrorCode {

	/**
	 * 获取模块名
	 * @return 模块名
	 */
	String getModule();

	/**
	 * 获取HTTP状态码
	 * @return 状态码
	 */
	int getHttpStatusCode();

	/**
	 * 获取说明信息
	 * @return 说明信息
	 */
	String getMessage();

}
