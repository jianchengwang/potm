package org.example.potm.framework.pojo;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.Data;

import java.util.Arrays;
import java.util.List;

/**
 * @author jianchengwang
 * @date 2023/3/30
 */
@Data
public class PageInfo {

    /**
     * 页数，从1开始计数
     */
    @Parameter(description = "页数，从1开始计数", required = true, example = "1")
    private int page = 1;

    /**
     * 每页数量
     */
    @Parameter(description = "每页数量", required = true, example = "20")
    private int size = 20;

    /**
     * 排序，默认使用降序，例如：name:asc按name字段进行升序排序
     */
    @Parameter(description = "排序，默认使用降序，例如：name:asc按name字段进行升序排序")
    List<String> sort;

    /**
     * 创建分页信息
     * @param page 页数，从1开始计数
     * @param size 每页数量
     * @return 分页信息
     */
    public static PageInfo of(int page, int size) {
        PageInfo pageInfo = new PageInfo();
        pageInfo.page = page;
        pageInfo.size = size;
        return pageInfo;
    }

    /**
     * 创建分页信息
     * @param page 页数，从1开始计数
     * @param size 每页数量
     * @param sort 排序，默认使用降序，例如：name:asc按name字段进行升序排序
     * @return 分页信息
     */
    public static PageInfo of(int page, int size, String... sort) {
        PageInfo pageInfo = new PageInfo();
        pageInfo.page = page;
        pageInfo.size = size;
        if (sort != null && sort.length > 0) {
            pageInfo.sort = Arrays.asList(sort);
        }
        return pageInfo;
    }

    public long getLimit() {
        if (page == 0) {
            return 0;
        }
        return (page - 1) * size;
    }

}
