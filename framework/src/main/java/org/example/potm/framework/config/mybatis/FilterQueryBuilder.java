package org.example.potm.framework.config.mybatis;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Stream;

/**
 * @author jianchengwang
 * @date 2023/4/13
 */
public class FilterQueryBuilder {

    private static final Set<String> FilterMatchMode = Set.of(
            "eq",
            "ne",
            "gt",
            "ge",
            "lt",
            "le",
            "sw",
            "ew",
            "in",
            "notIn",
            "isNull",
            "isNotNull",
            "between",
            "notBetween"
    );

    public static void build(String filters, QueryWrapper queryWrapper) {
        if(StringUtils.isNotEmpty(filters)) {
            Stream<String> filterStream = Arrays.stream(filters.split(";"));
            filterStream.forEach(filter -> {
                buildFilter(filter, queryWrapper);
            });
        }
    }
    private static void buildFilter(String filter, QueryWrapper queryWrapper) {
        FilterMatchMode.forEach(opt -> {
            if(filter.contains(opt)) {
                String field = filter.substring(0, filter.indexOf(opt));
                String value = filter.substring(filter.indexOf(opt) + opt.length());
                switch (opt) {
                    case "eq" -> {
                        queryWrapper.eq(field, value);
                    }
                    case "ne" -> {
                        queryWrapper.ne(field, value);
                    }
                    case "gt" -> {
                        queryWrapper.gt(field, value);
                    }
                    case "ge" -> {
                        queryWrapper.ge(field, value);
                    }
                    case "lt" -> {
                        queryWrapper.lt(field, value);
                    }
                    case "le" -> {
                        queryWrapper.le(field, value);
                    }
                    case "sw" -> {
                        queryWrapper.like(field, value + "%");
                    }
                    case "ew" -> {
                        queryWrapper.like(field, "%" + value);
                    }
                    case "in" -> {
                        queryWrapper.in(field, value.split(","));
                    }
                    case "notIn" -> {
                        queryWrapper.notIn(field, value);
                    }
                    case "isNull" -> {
                        queryWrapper.isNull(field);
                    }
                    case "isNotNull" -> {
                        queryWrapper.isNotNull(field);
                    }
                    case "between" -> {
                        String first = value.substring(0, value.indexOf(","));
                        String second = value.substring(value.indexOf(",") + 1);
                        queryWrapper.between(field, first, second);
                    }
                    case "notBetween" -> {
                        String first = value.substring(0, value.indexOf(","));
                        String second = value.substring(value.indexOf(",") + 1);
                        queryWrapper.notBetween(field, first, second);
                    }
                    default -> {

                    }
                }
            }
        });
    }
}
