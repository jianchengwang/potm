package com.example.potm.svc.core.domain.repository;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.potm.svc.core.interfaces.operate.query.SysDictQuery;
import org.example.potm.framework.config.dict.SysDict;
import org.example.potm.framework.config.dict.SysDictItem;
import org.example.potm.framework.pojo.PageInfo;

import java.util.List;

/**
 * @author jianchengwang
 * @date 2023/4/11
 */
public interface SysDictRepository {
    IPage<SysDict> page(PageInfo pageInfo, SysDictQuery param);
    List<SysDictItem> getItemList(String svcName, String dictKey);

    List<SysDict> fetchAll();
}
