package com.example.potm.svc.core.infrastructure.converter;

import com.example.potm.svc.core.infrastructure.db.po.SysUser;
import com.example.potm.svc.core.interfaces.auth.vo.UserInfoVO;
import org.example.potm.framework.config.permission.user.TokenUser;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author jianchengwang
 * @date 2023/3/31
 */
@Mapper
public interface SysUserConverter {
    SysUserConverter MAPPER = Mappers.getMapper( SysUserConverter.class );

    TokenUser toTokenUser(SysUser user);

    UserInfoVO toUserInfoVO(TokenUser tokenUser);
}
