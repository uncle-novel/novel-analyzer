package com.unclezs.novel.analyzer.utils;

import lombok.experimental.UtilityClass;

import java.util.Collection;
import java.util.Map;

/**
 * 集合工具
 *
 * @author blog.unclezs.com
 * @date 2020/12/20 7:21 下午
 */
@UtilityClass
public class CollectionUtil {
    /**
     * 集合是否为空
     *
     * @param collection /
     * @return /
     */
    public boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    /**
     * 集合是否不为空
     *
     * @param collection /
     * @return /
     */
    public boolean isNotEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }

    /**
     * Map是否为空
     *
     * @param map /
     * @return /
     */
    public boolean isEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    /**
     * Map是否不为空
     *
     * @param map /
     * @return /
     */
    public boolean isNotEmpty(Map<?, ?> map) {
        return !isEmpty(map);
    }
}
