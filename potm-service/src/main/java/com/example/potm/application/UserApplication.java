package com.example.potm.application;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.potm.domain.user.repository.UserRepository;
import com.example.potm.interfaces.operate.query.UserQuery;
import com.example.potm.interfaces.operate.vo.UserVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.framework.pojo.PageInfo;
import org.springframework.stereotype.Service;

/**
 * @author jianchengwang
 * @date 2023/3/31
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserApplication {

    private final UserRepository userRepository;

    public IPage<UserVO> page(PageInfo pageInfo, UserQuery param) {
        return userRepository.page(pageInfo, param);
    }
}
