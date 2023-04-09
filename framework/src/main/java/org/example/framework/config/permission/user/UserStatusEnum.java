package org.example.framework.config.permission.user;

import org.example.framework.pojo.IBaseEnum;

/**
 * @author jianchengwang
 * @date 2023/3/31
 */
public enum UserStatusEnum implements IBaseEnum<Integer> {
    /**
     * 正常
     */
    NORMAL(1, "正常"),
    /**
     * 冻结
     */
    FROZEN(2, "冻结"),
    /**
     * 注销
     */
    LOGOUT(3, "注销");

    private Integer value;
    private String description;

    UserStatusEnum(Integer value, String description) {
        this.value = value;
        this.description = description;
    }

    @Override
    public Integer getValue() {
        return this.value;
    }

    @Override
    public Object getDescription() {
        return this.description;
    }
}
