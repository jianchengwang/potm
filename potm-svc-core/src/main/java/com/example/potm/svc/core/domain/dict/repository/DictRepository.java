package com.example.potm.svc.core.domain.dict.repository;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.potm.svc.core.interfaces.operate.query.SysDictQuery;
import com.example.potm.svc.core.interfaces.operate.vo.SysDictItemVO;
import com.example.potm.svc.core.interfaces.operate.vo.SysDictVO;
import org.example.framework.pojo.PageInfo;

import java.util.List;

/**
 * @author jianchengwang
 * @date 2023/4/11
 */
public interface DictRepository {
    IPage<SysDictVO> page(PageInfo pageInfo, SysDictQuery param);
    List<SysDictItemVO> getItemList(String svcName, String dictKey);
}