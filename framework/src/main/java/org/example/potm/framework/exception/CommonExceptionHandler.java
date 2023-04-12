package org.example.potm.framework.exception;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import org.example.potm.framework.pojo.Response;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.internal.engine.ConstraintViolationImpl;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import javax.naming.SizeLimitExceededException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author jianchengwang
 * @date 2023/3/30
 */
@Slf4j
@RestControllerAdvice
public class CommonExceptionHandler {

    private final ErrorAttributes errorAttributes;

    private Field sourceField;

    // 内置异常校验消息模板位置
    private final String JAKARTA_VALIDATE_CONSTRAINTS = "jakarta.validation.constraints";

    private final String HIBERNATE_VALIDATE_CONSTRAINTS = "org.hibernate.validator.constraints";

    public CommonExceptionHandler(ErrorAttributes errorAttributes) {
        this.errorAttributes = errorAttributes;
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<?> handException(Throwable e, HttpServletRequest request) {
        if (e instanceof BindException || e instanceof MethodArgumentNotValidException
                || e instanceof HttpRequestMethodNotSupportedException || e instanceof HttpMessageNotReadableException
                || e instanceof ServletRequestBindingException) {
            return build400Error(request, e);

        }
        else if (e instanceof CommonException) {

            CommonException ex = (CommonException) e;

            // 客户端参数绑定错误，指定返回错误信息
            if (ex instanceof ClientBindException) {
                ClientBindException bx = (ClientBindException) ex;
                request.setAttribute("jakarta.servlet.error.status_code", HttpStatus.BAD_REQUEST.value());
                request.setAttribute(DefaultErrorAttributes.class.getName() + ".ERROR",
                        new BindException(bx.getBindingResult()));
                return build400Error(request, ex);
            }

            StringBuilder sb = new StringBuilder("请求url [{}] 出错");
            if (ex instanceof ServerException) {
                Object[] objs;
                Object[] args = ex.getArgs();
                if (args != null && args.length > 0) {
                    objs = new Object[args.length + 2];
                    objs[0] = request.getRequestURI();
                    sb.append("，异常参数：");
                    for (int i = 0; i < args.length; i++) {
                        sb.append("[{}] ");
                        objs[i + 1] = args[i];
                    }
                    objs[args.length + 1] = ex;
                }
                else {
                    objs = new Object[] { request.getRequestURI(), ex };
                }
                log.error(sb.toString(), objs);
            }
            else if (ex instanceof ClientException) {
                log.warn(sb.append("，ErrorCode [{}], ErrorMsg[{}]").toString(), request.getRequestURI(), ex.getCode(),
                        ex.getMessage());
                if (log.isDebugEnabled()) {
                    log.debug(ex.getMessage(), ex);
                }
            }
        }

        Response<?> fail = Response.fail(e);
        return new ResponseEntity<>(fail, HttpStatus.valueOf(200));
    }

    @ExceptionHandler({ MaxUploadSizeExceededException.class, SizeLimitExceededException.class })
    public ResponseEntity<?> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        ClientException clientException = new ClientException(FrameworkErrorCode.MAX_UPLOAD_SIZE_OVERFLOW);
        Response<?> fail = Response.fail(clientException);
        return new ResponseEntity<>(fail, HttpStatus.valueOf(200));
    }

    private ResponseEntity<?> build400Error(HttpServletRequest request, Throwable e) {
        e.printStackTrace();
        FieldError fieldError = null;
        if (e instanceof BindException) {
            fieldError = ((BindException) e).getBindingResult().getFieldError();
        }
        Map<String, Object> errorAttributes = getErrorAttributes(request);
        Object errors = errorAttributes.get("errors");
        String errorMessage = build400ErrorMessage(errorAttributes, fieldError);
        if (StringUtils.isEmpty(errorMessage)) {
            errorMessage = e.getMessage();
        }
        Response<?> fail = Response.fail400("ARGUMENT_VALIDATION_FAILED", errorMessage,
                errors != null ? errors : errorAttributes);
        return new ResponseEntity<>(fail, HttpStatus.valueOf(200));
    }

    private Map<String, Object> getErrorAttributes(HttpServletRequest request) {
        WebRequest webRequest = new ServletWebRequest(request);
        return this.errorAttributes.getErrorAttributes(webRequest, ErrorAttributeOptions.defaults());
    }

    /**
     * 请求参数错误信息 如果是对象嵌套集合参数请配置message自定义校验消息，比如@NotNull(message = "错误信息")
     * @param errorAttributes
     * @return
     */
    private String build400ErrorMessage(Map<String, Object> errorAttributes, FieldError fieldError) {
        String errorMessage = "请求参数错误";
        Object errors = errorAttributes.get("errors");
        if (fieldError == null && errors != null) {
            fieldError = (FieldError) ((List) errors).get(0);
        }
        if (fieldError != null) {
            if (StrUtil.isNotEmpty(fieldError.getDefaultMessage())) {
                String fieldName = fieldError.getField();
                try {
                    if (sourceField == null) {
                        sourceField = FieldError.class.getSuperclass().getDeclaredField("source");
                        sourceField.setAccessible(true);
                    }
                    ConstraintViolationImpl cv = (ConstraintViolationImpl) sourceField.get(fieldError);
                    if (cv.getMessageTemplate() != null
                            && !cv.getMessageTemplate().contains(JAKARTA_VALIDATE_CONSTRAINTS)
                            && !cv.getMessageTemplate().contains(HIBERNATE_VALIDATE_CONSTRAINTS)) {
                        return fieldError.getDefaultMessage();
                    }
                    Class beanClass = cv.getRootBeanClass();
                    List<Field> fields = Arrays.stream(ReflectUtil.getFields(beanClass)).toList();
                    if (CollectionUtil.isNotEmpty(fields)) {
                        final String fieldNameStr = fieldName;
                        Field field = fields.stream().filter(f -> f.getName().equals(fieldNameStr)).findFirst().get();
                        Schema apiModelProperty = field.getAnnotation(io.swagger.v3.oas.annotations.media.Schema.class);
                        if (apiModelProperty != null) {
                            String fieldNameValue = apiModelProperty.description();
                            if (StrUtil.isNotEmpty(fieldNameValue)) {
                                fieldName = fieldNameValue;
                            }
                        }
                        return fieldName + fieldError.getDefaultMessage();
                    }
                }
                catch (Exception e) {
                    return errorMessage;
                }
            }
        }
        return errorMessage;
    }

}
