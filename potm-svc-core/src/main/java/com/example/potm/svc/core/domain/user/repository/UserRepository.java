package com.example.potm.svc.core.domain.user.repository;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.potm.svc.core.infrastructure.user.db.po.User;
import com.example.potm.svc.core.interfaces.operate.dto.UserSaveDTO;
import com.example.potm.svc.core.interfaces.operate.query.UserQuery;
import com.example.potm.svc.core.interfaces.operate.vo.UserVO;
import org.example.framework.pojo.PageInfo;

/**
 * @author jianchengwang
 * @date 2023/3/31
 */
public interface UserRepository extends IService<User> {

    IPage<UserVO> page(PageInfo pageInfo, UserQuery param);

    User findById(Long id);
    User findByUsername(String username);

    void save(UserSaveDTO param);

    void generateUser(Long generateNum);

    void deleteById(Long id);
}
