package com.example.potm.svc.core.infrastructure.dict.repository;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.potm.svc.core.domain.dict.repository.DictRepository;
import com.example.potm.svc.core.infrastructure.dict.db.dao.SysDictDao;
import com.example.potm.svc.core.interfaces.operate.query.SysDictQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.framework.config.dict.SysDict;
import org.example.framework.config.dict.SysDictItem;
import org.example.framework.config.mybatis.MpHelper;
import org.example.framework.pojo.PageInfo;
import org.example.framework.utils.PageUtils;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * @author jianchengwang
 * @date 2023/4/11
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class DictRepositoryImpl implements DictRepository {

    private final SysDictDao sysDictDao;

    @Override
    public IPage<SysDict> page(PageInfo pageInfo, SysDictQuery param) {
        LambdaQueryWrapper<SysDict> query = MpHelper.lambdaQuery("a", BeanUtil.copyProperties(param, SysDict.class));
        return sysDictDao.page(PageUtils.buildPage(pageInfo), param, query);
    }

    @Override
    public List<SysDictItem> getItemList(String svcName, String dictKey) {
        return sysDictDao.getItemList(svcName, dictKey);
    }

    @Override
    public List<SysDict> fetchAll() {

        List<SysDict> dictList = sysDictDao.fetchAll();
        List<SysDictItem> itemList = sysDictDao.fetchItemAll();
        for(SysDict dict: dictList) {
            dict.setItemList(itemList.stream()
                    .filter(item -> Objects.equals(item.getSvcName(), dict.getSvcName()) && Objects.equals(item.getDictKey(), dict.getDictKey()))
                    .sorted(Comparator.comparing(SysDictItem::getSortOrder)).toList());
        }
        return dictList;
    }
}
