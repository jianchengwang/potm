package com.example.potm.svc.core.application;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.potm.svc.core.domain.dict.repository.DictRepository;
import com.example.potm.svc.core.interfaces.operate.query.SysDictQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.potm.framework.config.dict.DictRedisOperator;
import org.example.potm.framework.config.dict.SysDict;
import org.example.potm.framework.config.dict.SysDictItem;
import org.example.potm.framework.pojo.PageInfo;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author jianchengwang
 * @date 2023/4/11
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class DictApplication {

    private final DictRepository dictRepository;
    private final DictRedisOperator dictRedisOperator;

    public IPage<SysDict> page(PageInfo pageInfo, SysDictQuery param) {
        return dictRepository.page(pageInfo, param);
    }

    public List<SysDict> loadAll(boolean forceLoadFromDb) {
        List<SysDict> dictList;
        if(!forceLoadFromDb) {
            dictList = dictRedisOperator.loadAll();
            if(CollectionUtil.isNotEmpty(dictList)) {
                return dictList;
            }
        }
        dictList = dictRepository.fetchAll();
        dictRedisOperator.putAll(dictList);
        return dictList;
    }

    public List<SysDictItem> getItemList(String svcName, String dictKey) {
        List<SysDictItem> itemList = dictRedisOperator.getItemList(svcName, dictKey);
        if(CollectionUtil.isNotEmpty(itemList)) {
            return itemList;
        }
        itemList = dictRepository.getItemList(svcName, dictKey);
        dictRedisOperator.putItemList(svcName, dictKey, itemList);
        return itemList;
    }
}
