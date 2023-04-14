package com.example.potm.svc.core.infrastructure.db.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.example.potm.framework.config.permission.user.UserScopeEnum;
import org.example.potm.framework.config.permission.user.UserStatusEnum;
import org.example.potm.framework.pojo.PO;

/**
 * @author jianchengwang
 * @date 2023/3/30
 */
@Data
@TableName("sys_user")
public class SysUser implements PO {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private String username;
    private String nickname;
    private String mobile;
    private String password;
    private String passwordSalt;
    private UserScopeEnum userScope;
    private UserStatusEnum userStatus;

    // getters and setters
}
