package org.example.potm.framework.config.dict;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.example.potm.framework.pojo.IBaseEnum;
import org.reflections.Reflections;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author jianchengwang
 * @date 2023/4/11
 */
@Slf4j
public class DictProcessor {
    private final String configSvcName;
    private final DictProperties dictProperties;
    private final JdbcTemplate jdbcTemplate;

    private ConcurrentHashMap<String, SysDictItem> LocalMap = new ConcurrentHashMap();

    public DictProcessor(String configSvcName, DictProperties dictProperties,
                         JdbcTemplate jdbcTemplate) {
        this.configSvcName = configSvcName;
        this.dictProperties = dictProperties;
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostConstruct
    public void syncDb() throws IOException {
        Object[] enumsPackages = dictProperties.getEnumsPackage().split(",");
        Reflections reflections = new Reflections(enumsPackages);
        Set<Class<?>> classesList = reflections.getTypesAnnotatedWith(DictEnum.class);
        List<SysDict> dictList = new ArrayList<>();
        List<SysDictItem> dictItemList = new ArrayList<>();
        classesList.forEach(clazz -> {
            DictEnum dictEnum = clazz.getAnnotation(DictEnum.class);
            SysDict dict = new SysDict();
            String svcName = dictEnum.svc();
            if(StringUtils.isEmpty(svcName)) {
                svcName = configSvcName;
            }
            dict.setSvcName(svcName);
            dict.setDictKey(dictEnum.dictKey());
            dict.setDescription(dictEnum.description());
            dict.setRemark(dictEnum.description());
            dict.setSystemFlag(true);
            dict.setEnumFlag(true);
            IBaseEnum[] itemEnumList = (IBaseEnum[]) clazz.getEnumConstants();
            List<SysDictItem> itemList = new ArrayList<>();
            var sortOrder = 0;
            for(IBaseEnum itemEnum: itemEnumList) {
                SysDictItem item = new SysDictItem();
                item.setSvcName(svcName);
                item.setDictKey(dictEnum.dictKey());
                item.setValue(itemEnum.getValue().toString());
                item.setLabel(itemEnum.getDescription().toString());
                item.setType(itemEnum.getName());
                item.setSortOrder(sortOrder++);
                item.setDescription(itemEnum.getDescription().toString());
                item.setRemark((String) itemEnum.getDescription());
                dictItemList.add(item);
                LocalMap.putIfAbsent(String.format("%s:%s:%s", item.getSvcName(), item.getDictKey(), item.getValue()), item);
            }
            dictList.add(dict);
            dictItemList.addAll(itemList);
        });
        try {
            if(dictProperties.isSyncDb()) {
                List<Object[]> dictRecords = dictList.stream()
                        .map(r -> new Object[]{
                                r.getSvcName(), r.getDictKey(), r.getDescription(), r.getRemark(), r.getSystemFlag(), r.getEnumFlag()
                        })
                        .collect(Collectors.toList());

                List<Object[]> dictItemRecords = dictItemList.stream()
                        .map(r -> new Object[]{
                                r.getSvcName(), r.getDictKey(), r.getValue(), r.getLabel(), r.getType(), r.getDescription(), r.getSortOrder(), r.getRemark()
                        })
                        .collect(Collectors.toList());
                jdbcTemplate.batchUpdate(DictSqlTemplate.REPLACE_INSERT_DICT_SQL, dictRecords);
                jdbcTemplate.batchUpdate(DictSqlTemplate.REPLACE_INSERT_DICT_ITEM_SQL, dictItemRecords);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("同步枚举字典异常：{}", e.getMessage());
        }
    }

    public String getDictItemLabel(String svcName, String dictKey, Object value) {
        if(StringUtils.isEmpty(svcName)) {
            svcName = configSvcName;
        }
        SysDictItem dictItem = LocalMap.getOrDefault(String.format("%s:%s:%s", svcName, dictKey, value), null);
        if(dictItem != null) {
            return dictItem.getLabel();
        }
        return null;
    }
}
