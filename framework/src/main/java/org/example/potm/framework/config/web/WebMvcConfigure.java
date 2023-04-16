package org.example.potm.framework.config.web;

import cn.hutool.core.date.DatePattern;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import lombok.RequiredArgsConstructor;
import org.example.potm.framework.config.dict.DictProcessor;
import org.example.potm.framework.config.dict.VoJsonSerializer;
import org.example.potm.framework.config.jackson.CustomJavaTimeModule;
import org.example.potm.framework.config.satoken.SatokenHandlerInterceptor;
import org.example.potm.framework.pojo.VO;
import org.example.potm.framework.utils.JSONUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @author jianchengwang
 * @date 2023/3/30
 */
@Configuration(proxyBeanMethods = false)
@RequiredArgsConstructor
public class WebMvcConfigure implements WebMvcConfigurer {

    private final RedisTemplate redisTemplate;
    private final DictProcessor dictProcessor;

    /**
     * 增加GET请求参数中时间类型转换
     * <ul>
     * <li>HH:mm:ss -> LocalTime</li>
     * <li>yyyy-MM-dd -> LocalDate</li>
     * <li>yyyy-MM-dd HH:mm:ss -> LocalDateTime</li>
     * </ul>
     * @param registry
     */
    @Override
    public void addFormatters(FormatterRegistry registry) {
        DateTimeFormatterRegistrar registrar = new DateTimeFormatterRegistrar();
        registrar.setTimeFormatter(DatePattern.NORM_TIME_FORMATTER);
        registrar.setDateFormatter(DatePattern.NORM_DATE_FORMATTER);
        registrar.setDateTimeFormatter(DatePattern.NORM_DATETIME_FORMATTER);
        registrar.registerFormatters(registry);
    }

//    @Override
//    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
//        MappingJackson2HttpMessageConverter jackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
//        ObjectMapper objectMapper = jackson2HttpMessageConverter.getObjectMapper();
//        configObjectMapper(objectMapper);
//        // 设置中文编码格式
//        List<MediaType> list = new ArrayList<>();
//        list.add(MediaType.APPLICATION_JSON);
//        jackson2HttpMessageConverter.setSupportedMediaTypes(list);
//        SimpleModule simpleModule = new SimpleModule();
//        simpleModule.addSerializer(VO.class, new VoJsonSerializer(dictProcessor));
//        // Set类型字段用LinkedHashSet处理，保证有序
//        simpleModule.addAbstractTypeMapping (Set.class, LinkedHashSet.class);
//        objectMapper.registerModule(simpleModule);
//        jackson2HttpMessageConverter.setObjectMapper(objectMapper);
//        converters.add(0, jackson2HttpMessageConverter);
//        converters.add(new StringHttpMessageConverter());
//    }

    private void configObjectMapper(ObjectMapper objectMapper) {
        String timeZone = "GMT+8";

        final String DATE_FORMAT = "yyyy-MM-dd";
        final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

        //json中多余的参数不报错，不想要可以改掉
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //设置全局的时间转化
        objectMapper.setDateFormat(new SimpleDateFormat(DATETIME_FORMAT));
        // 解决时区差8小时问题
        objectMapper.setTimeZone(TimeZone.getTimeZone(timeZone));

        SimpleModule simpleModule = new SimpleModule();
        //日期转换
        simpleModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DATETIME_FORMAT)));
        simpleModule.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern(DATE_FORMAT)));
        simpleModule.addSerializer(LocalTime.class, LocalTimeSerializer.INSTANCE);
        simpleModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(DATETIME_FORMAT)));
        simpleModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern(DATE_FORMAT)));
        simpleModule.addDeserializer(LocalTime.class, LocalTimeDeserializer.INSTANCE);

        objectMapper.registerModule(simpleModule);
    }

//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**").allowCredentials(true).allowedOriginPatterns("*").allowedHeaders("*")
//                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS").maxAge(3600);
//    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 添加表单重复提交拦截器
        registry.addInterceptor(new RepeatSubmitFormHandlerInterceptor(redisTemplate))
                .addPathPatterns("/api/**");
        // 添加satoken校验
        registry.addInterceptor(new SatokenHandlerInterceptor()).addPathPatterns("/api/**");
    }

    @Bean
    public LocaleResolver localeResolverChinese() {
        SessionLocaleResolver slr = new SessionLocaleResolver();
        slr.setDefaultLocale(Locale.CHINESE);
        return slr;
    }
}

