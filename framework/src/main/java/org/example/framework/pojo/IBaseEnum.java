package org.example.framework.pojo;

import com.baomidou.mybatisplus.annotation.IEnum;

import java.io.Serializable;

/**
 * @author jianchengwang
 * @date 2023/3/31
 */
public interface IBaseEnum<T extends Serializable> extends IEnum<T> {
    Object getDescription();
    default Object getName() {
        return this.toString();
    }
}
