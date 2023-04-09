package com.example.potm.infrastructure.common.converter;

import com.example.potm.infrastructure.user.db.po.User;
import com.example.potm.interfaces.auth.vo.UserInfoVO;
import org.example.framework.config.permission.user.TokenUser;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author jianchengwang
 * @date 2023/3/31
 */
@Mapper
public interface UserConverter {
    UserConverter MAPPER = Mappers.getMapper( UserConverter.class );

    TokenUser toTokenUser(User user);

    UserInfoVO toUserInfoVO(TokenUser tokenUser);
}
