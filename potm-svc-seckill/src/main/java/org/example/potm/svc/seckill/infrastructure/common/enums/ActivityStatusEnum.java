package org.example.potm.svc.seckill.infrastructure.common.enums;

import org.example.framework.pojo.IBaseEnum;

/**
 * 活动状态枚举
 * @author jianchengwang
 * @date 2023/3/31
 */
public enum ActivityStatusEnum implements IBaseEnum<Integer> {
    INIT(0, "未开始"),
    STARTED(1, "进行中"),
    END(2, "已结束"),
    BROKEN(3, "终止")
    ;

    private final Integer value;
    private final String description;

    ActivityStatusEnum(Integer value, String description) {
        this.value = value;
        this.description = description;
    }

    @Override
    public Object getDescription() {
        return description;
    }

    @Override
    public Integer getValue() {
        return value;
    }
}
