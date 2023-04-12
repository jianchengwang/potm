package org.example.potm.framework.exception;

import lombok.Getter;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

/**
 * @author jianchengwang
 * @date 2023/3/14
 */
@Getter
public class ClientBindException extends ClientException {

	private final Object object;

	private final String field;

	private final Object value;

	private final BindingResult bindingResult;

	public ClientBindException(Object object, String field, Object value, Enum<? extends ErrorCode> errorCode,
			Throwable throwable) {
		super(errorCode, throwable, object, field, value);
		this.object = object;
		this.field = field;
		this.value = value;
		bindingResult = new BeanPropertyBindingResult(object, getObjectName());
		bindingResult.rejectValue(field, getSimpleCode(), getCodeMessage());
	}

	public String getObjectName() {
		String simpleName = this.object.getClass().getSimpleName();
		return simpleName.substring(0, 1).toLowerCase() + simpleName.substring(1);
	}

}
