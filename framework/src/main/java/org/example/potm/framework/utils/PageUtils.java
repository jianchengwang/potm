package org.example.potm.framework.utils;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.potm.framework.pojo.PageInfo;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author jianchengwang
 * @date 2022/10/17
 */
public class PageUtils {

    /**
     * 构建分页对象
     *
     * @param pageInfo 分页信息
     * @param <T> 分页数据类型
     * @return 分页对象
     */
    public static <T> IPage<T> buildPage(PageInfo pageInfo) {
        return buildPage(pageInfo.getPage(), pageInfo.getSize(), pageInfo.getSort(), null);
    }

    /**
     * 构建分页对象
     *
     * @param pageInfo 分页信息
     * @param customerFieldName 自定义字段，排序的时候不需要进行underLine转换
     * @param <T> 分页数据类型
     * @return 分页对象
     */
    public static <T> IPage<T> buildPage(PageInfo pageInfo, List<String> customerFieldName) {
        return buildPage(pageInfo.getPage(), pageInfo.getSize(), pageInfo.getSort(), customerFieldName);
    }

    /**
     * 构建分页对象
     *
     * @param page 页数
     * @param size 每页数量
     * @param <T> 分页数据类型
     * @return 分页对象
     */
    public static <T> IPage<T> buildPage(int page, int size) {
        return buildPage(page, size, null, true, null);
    }

    /**
     * 构建分页对象
     *
     * @param page 页数
     * @param size 每页数量
     * @param isSearchCount 是否进行 count 查询
     * @param <T> 分页数据类型
     * @return 分页对象
     */
    public static <T> IPage<T> buildPage(int page, int size, boolean isSearchCount) {
        return buildPage(page, size, null, isSearchCount, null);
    }

    /**
     * 构建分页对象
     *
     * @param page 页数
     * @param size 每页数量
     * @param sort 排序，默认使用降序，例如：name:asc按name字段进行升序排序
     * @param customerFieldName 自定义字段，排序的时候不需要进行underLine转换
     * @param <T> 分页数据类型
     * @return 分页对象
     */
    public static <T> IPage<T> buildPage(int page, int size, List<String> sort, List<String> customerFieldName) {
        return buildPage(page, size, sort, true, customerFieldName);
    }

    /**
     * 构建分页对象
     *
     * @param page 页数
     * @param size 每页数量
     * @param sort 排序，默认使用降序，例如：name:asc:按name字段进行升序排序
     * @param isSearchCount 是否进行 count 查询
     * @param customerFieldName 自定义字段，排序的时候不需要进行underLine转换
     * @param <T> 分页数据类型
     * @return 分页对象
     */
    public static <T> IPage<T> buildPage(int page, int size, List<String> sort, boolean isSearchCount, List<String> customerFieldName) {
        Page<T> pageInfo = new Page<>(page, size);
        pageInfo.setSearchCount(isSearchCount);
        if (sort != null) {
            Set<String> exist = new HashSet<>();
            for (String order : sort) {
                if (order != null && !order.trim().isEmpty()) {
                    String[] split = order.split("\\s*:\\s*");
                    boolean asc = split.length > 1 && "asc".equalsIgnoreCase(split[1]);
                    String key = split[0] + '-' + asc;
                    if (!exist.contains(key)) {
                        exist.add(key);
                        String column = split[0];
                        if(CollectionUtils.isEmpty(customerFieldName) || !customerFieldName.contains(column)) {
                            column = StringUtils.camelToUnderline(split[0]);
                        }
                        pageInfo.addOrder(asc ? OrderItem.asc(column) : OrderItem.desc(column));
                    }
                }
            }
        }
        return pageInfo;
    }


    /**
     * 获取分页起始位置 limit起始序号 从0开始
     * @param total 记录总数量
     * @param pageInfo 分页信息
     * @return -1 超出记录总数量，无需提取
     */
    public static int getPageStartIndex(int total, PageInfo pageInfo) {
        if (null == pageInfo) pageInfo = PageInfo.of(1, 10);
        int startIndex = -1;
        if (total >= 0) {
            startIndex = (pageInfo.getPage() - 1) * pageInfo.getSize();
            if (startIndex >= total)
                startIndex = -1;
        }
        return startIndex;
    }

    /**
     * 获取全部记录
     *
     * @param supplier 获取列表
     * @param <T> 实体
     * @return 分页信息
     */
    public static <T> IPage<T> getAllRecord(Supplier<IPage<T>> supplier) {
        if (supplier == null) {
            return null;
        }

        IPage<T> page;
        List<T> list = null;
        while ((page = supplier.get()) != null && page.getCurrent() < page.getPages()) {
            if (list == null) {
                list = new ArrayList<>(Long.valueOf(page.getTotal()).intValue());
            }
            list.addAll(page.getRecords());
        }
        if (page != null && list != null) {
            list.addAll(page.getRecords());
            page.setRecords(list);
        }
        return page;
    }

}
