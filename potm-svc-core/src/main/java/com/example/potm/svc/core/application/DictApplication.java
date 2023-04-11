package com.example.potm.svc.core.application;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.potm.svc.core.domain.dict.repository.DictRepository;
import com.example.potm.svc.core.interfaces.operate.query.SysDictQuery;
import com.example.potm.svc.core.interfaces.operate.vo.SysDictItemVO;
import com.example.potm.svc.core.interfaces.operate.vo.SysDictVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.framework.pojo.PageInfo;
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

    public IPage<SysDictVO> page(PageInfo pageInfo, SysDictQuery param) {
        return dictRepository.page(pageInfo, param);
    }

    public List<SysDictItemVO> getItemList(String svcName, String dictKey) {
        return dictRepository.getItemList(svcName, dictKey);
    }
}
