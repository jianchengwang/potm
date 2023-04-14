package com.example.potm.svc.core.application;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.potm.svc.core.domain.repository.SysUserRepository;
import com.example.potm.svc.core.interfaces.operate.dto.SysUserSaveDTO;
import com.example.potm.svc.core.interfaces.operate.query.SysUserQuery;
import com.example.potm.svc.core.interfaces.operate.vo.SysUserVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.potm.framework.pojo.PageInfo;
import org.springframework.stereotype.Service;

/**
 * @author jianchengwang
 * @date 2023/3/31
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SysUserApplication {

    private final SysUserRepository userRepository;

    public IPage<SysUserVO> page(PageInfo pageInfo, SysUserQuery param) {
        return userRepository.page(pageInfo, param);
    }

    public void save(SysUserSaveDTO param) {
        userRepository.save(param);
    }

    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }
}
