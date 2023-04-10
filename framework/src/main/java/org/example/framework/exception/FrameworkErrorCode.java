package org.example.framework.exception;

import lombok.Getter;

/**
 * @author jianchengwang
 * @date 2023/3/14
 */
@Getter
public enum FrameworkErrorCode implements ErrorCode {

	/**
	 * Feign证书加载失败
	 */
	HTTP_FEIGN_SSL(500, "Feign证书加载失败"),
	/**
	 * 实例ID为空
	 */
	DB_INSTANCE_ID_EMPTY(500, "实例ID为空"),
	/**
	 * 表字段值不唯一
	 */
	DB_TABLE_DUPLICATE_KEY(500, "表字段值不唯一"),
	/**
	 * 数据库源数据获取失败
	 */
	DB_META_DATA(500, "数据库源数据获取失败"),
	/**
	 * 对象转换失败
	 */
	POJO_CONVERT(500, "对象转换失败"),
	/**
	 * 获取POJO ID失败
	 */
	ID_POJO_GET(500, "获取POJO ID失败"),
	/**
	 * ID生成失败
	 */
	ID_GENERATE(500, "ID生成失败"),

	/**
	 * 索引超过限制
	 */
	INDEX_OVERFLOW(500, "索引号超过限制"),

	/**
	 * 服务端异常
	 */
	SERVER_ERROR(500, "服务端异常"),

	/**
	 * 参数校验错误
	 */
	PARAM_VALIDATE_ERROR(400, "参数校验失败"),

	/**
	 * 文件上传大小超过限制
	 */
	MAX_UPLOAD_SIZE_OVERFLOW(400, "文件上传大小超过限制"),

	/**
	 * token认证失效
	 */
	UN_AUTHORIZED(401, "用户未认证"),

	SIGN_ERROR(401, "签名错误"),

	/**
	 * 权限校验失败
	 */
	USER_NOT_PERMISSION(403, "用户没有该资源权限，请授权后重试"),

	/**
	 * 资源不存在
	 */
	RESOURCE_NOT_FOUND(404, "资源不存在"),

	/**
	 * 重复提交表单，请等待早先数据处理完成
	 */
	REPEAT_SUBMIT_FORM_BY_ONE_USER(429, "重复提交表单，请等待早先数据处理完成"),

	LEGAL_REQUEST(430, "非法访问"),
	NOT_ALLOW(431, "不允许操作"),

	;

	private final int httpStatusCode;

	private final String message;

	FrameworkErrorCode(int httpStatusCode, String message) {
		this.httpStatusCode = httpStatusCode;
		this.message = message;
	}

	@Override
	public String getModule() {
		return "FRAMEWORK";
	}

}
