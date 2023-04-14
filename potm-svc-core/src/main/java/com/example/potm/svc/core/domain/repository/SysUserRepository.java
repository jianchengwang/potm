package com.example.potm.svc.core.domain.repository;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.potm.svc.core.infrastructure.db.po.SysUser;
import com.example.potm.svc.core.interfaces.operate.dto.SysUserSaveDTO;
import com.example.potm.svc.core.interfaces.operate.query.SysUserQuery;
import com.example.potm.svc.core.interfaces.operate.vo.SysUserVO;
import org.example.potm.framework.pojo.PageInfo;

/**
 * @author jianchengwang
 * @date 2023/3/31
 */
public interface SysUserRepository extends IService<SysUser> {

    IPage<SysUserVO> page(PageInfo pageInfo, SysUserQuery param);

    SysUser findById(Long id);
    SysUser findByUsername(String username);

    void save(SysUserSaveDTO param);

    void generateUser(Long generateNum);

    void deleteById(Long id);
}
