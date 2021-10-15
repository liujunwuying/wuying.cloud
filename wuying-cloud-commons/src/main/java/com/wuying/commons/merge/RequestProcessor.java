package com.wuying.commons.merge;

import java.util.List;

/**
 * 合并处理接口
 * @author wuying
 * @since 1.0.0
 * @date 2021-09-30
 */
public interface RequestProcessor<T> {
    /**
     * 合并处理接口
     * @param list 处理列表
     */
    void process(List<T> list);
}
