package org.example.potm.framework.pojo;

import com.baomidou.mybatisplus.annotation.IEnum;

import java.io.Serializable;

/**
 * @author jianchengwang
 * @date 2023/3/31
 */
public interface IBaseEnum<T extends Serializable> extends IEnum<T> {
    Object getDescription();
    default String getName() {
        return this.toString();
    }
}
