package org.example.potm.framework.config.dict;

import com.baomidou.mybatisplus.core.toolkit.ReflectionKit;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.example.potm.framework.exception.ClientException;
import org.example.potm.framework.exception.FrameworkErrorCode;
import org.example.potm.framework.pojo.IBaseEnum;
import org.example.potm.framework.pojo.VO;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author jianchengwang
 * @date 2023/4/15
 */
@JsonComponent
public class VoJsonSerializer extends JsonSerializer<VO> {

    private final String GET = "get";
    private final String IS = "is";
    private final String CLASS = "class";
    private final String DICT_LABEL_PREFIX = "DictLabel";

    private final DictProcessor dictProcessor;

    public VoJsonSerializer(DictProcessor dictProcessor) {
        this.dictProcessor = dictProcessor;
    }

    @Override
    public void serialize(VO obj, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        try {
            if(obj == null) {
                return;
            }
            Class clazz = obj.getClass();
            // 得到所有字段
            List<Field> fields = ReflectionKit.getFieldList(clazz);
            Map<String, Field> fieldsMap = fields.stream().collect(Collectors.toMap(Field::getName, a->a));
            // 得到所有get方法
            List<Method> methods = Arrays.stream(clazz.getMethods())
                    .filter(method -> method.getName().startsWith(GET) || method.getName().startsWith(IS))
                    .collect(Collectors.toList());
            gen.writeStartObject();
            for (Field f : fields) {
                JsonIgnore annotation = f.getAnnotation(JsonIgnore.class);
                if(annotation !=null){
                    continue;
                }
                f.setAccessible(true);
                Object value = f.get(obj);
                if(f.getAnnotation(DictInfo.class) != null) {
                    if (value instanceof IBaseEnum) {
                        String svc = "";
                        IBaseEnum baseEnum = (IBaseEnum) value;
                        DictEnum dictEnum = baseEnum.getClass().getAnnotation(DictEnum.class);
                        String dictKey = dictEnum.dictKey();
                        if(dictEnum != null) {
                            svc = dictEnum.svc();
                        }
                        String dictItemLabel = dictProcessor.getDictItemLabel(svc, dictKey, baseEnum.getValue());
                        String dictValFieldName = f.getName() + DICT_LABEL_PREFIX;
                        gen.writeObjectField(dictValFieldName, dictItemLabel);
                    }
                }
            }
            // get方法如果不是简单的getter，涉及复杂的操作逻辑，请自行保证不会出现异常
            for(Method method: methods) {
                String fieldName;
                if(method.getName().startsWith(GET)) {
                    fieldName = StringUtils.firstToLowerCase(method.getName().substring(GET.length()));
                } else {
                    fieldName = StringUtils.firstToLowerCase(method.getName().substring(IS.length()));
                }
                Field field = fieldsMap.get(fieldName);
                if(field !=null && field.getAnnotation(JsonIgnore.class) != null){
                    continue;
                }

                if(!CLASS.equals(fieldName)) {
                    method.setAccessible(true);
                    Object value = method.invoke(obj);
                    gen.writeObjectField(fieldName, value);
                }
            }
            gen.writeEndObject();
        } catch (Exception e) {
            e.printStackTrace();
            throw new ClientException(FrameworkErrorCode.POJO_CONVERT, e.getMessage(), e);
        }
    }
}
