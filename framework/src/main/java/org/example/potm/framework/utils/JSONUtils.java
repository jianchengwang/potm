package org.example.potm.framework.utils;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.example.potm.framework.config.jackson.CustomJavaTimeModule;
import org.example.potm.framework.exception.ClientException;
import org.example.potm.framework.exception.FrameworkErrorCode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

/**
 * @author jianchengwang
 * @date 2023/4/9
 */
@Slf4j
@UtilityClass
public class JSONUtils {
    /**
     * JSON转换对象
     */
    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        buildObjectMapper(OBJECT_MAPPER, false);
    }

    public static JavaType getCollectionType(Class<?> collectionClass, Class<?>... elementClasses) {
        return OBJECT_MAPPER.getTypeFactory().constructParametricType(collectionClass, elementClasses);
    }

    /**
     * 对象转json字符串
     * @param obj 对象实体
     * @return json字符串
     */
    public static String convertToString(Object obj) {
        String str = "";
        if (null == obj) return str;
        try {
            str = OBJECT_MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new ClientException(FrameworkErrorCode.POJO_CONVERT, e);
        }
        return str;
    }

    /**
     * JSON字符串转对象
     *
     * @param json JSON字符串
     * @param clazz 类型Class
     * @param <T> 类型
     * @return 对象
     */
    public static <T> T json2Object(String json, Class<T> clazz) {
        try {
            return OBJECT_MAPPER.readValue(json, clazz);
        } catch (IOException e) {
            throw new ClientException(FrameworkErrorCode.POJO_CONVERT, e);
        }
    }

    /**
     * JSON字符串转对象
     *
     * @param json JSON字符串
     * @param valueTypeRef 类型
     * @param <T> 类型
     * @return 对象
     */
    public static <T> T json2Object(String json, TypeReference<T> valueTypeRef) {
        try {
            return OBJECT_MAPPER.readValue(json, valueTypeRef);
        } catch (IOException e) {
            throw new ClientException(FrameworkErrorCode.POJO_CONVERT, e);
        }
    }

    public static <T> List<T> json2List(String json, Class<T> clazz) {
        try {
            return OBJECT_MAPPER.readValue(json, getCollectionType(ArrayList.class, clazz));
        } catch (IOException e) {
            throw new ClientException(FrameworkErrorCode.POJO_CONVERT, e);
        }
    }

    public static String jsonString(Object object) {
        try {
            return OBJECT_MAPPER.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new ClientException(FrameworkErrorCode.POJO_CONVERT, e);
        }
    }

    public static ObjectMapper buildObjectMapper(ObjectMapper objectMapper, boolean redis) {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        objectMapper.registerModule(new CustomJavaTimeModule());
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        if(redis) {
            // 此项必须配置，否则如果序列化的对象里边还有对象，会报如下错误：
            //     java.lang.ClassCastException: java.util.LinkedHashMap cannot be cast to XXX
            objectMapper.activateDefaultTyping(
                    objectMapper.getPolymorphicTypeValidator(),
                    ObjectMapper.DefaultTyping.NON_FINAL,
                    JsonTypeInfo.As.PROPERTY);
        }
        return objectMapper;
    }
}
