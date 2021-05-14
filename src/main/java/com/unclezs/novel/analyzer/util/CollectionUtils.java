package com.unclezs.novel.analyzer.util;

import lombok.experimental.UtilityClass;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * 集合工具
 *
 * @author blog.unclezs.com
 * @date 2020/12/20 7:21 下午
 */
@UtilityClass
public class CollectionUtils {
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

  /**
   * 新建一个HashSet
   *
   * @param <T> 集合元素类型
   * @param ts  元素数组
   * @return HashSet对象
   */
  @SafeVarargs
  public <T> Set<T> newSet(T... ts) {
    return set(false, ts);
  }

  /**
   * 新建一个LinkedHashSet
   *
   * @param <T> 集合元素类型
   * @param ts  元素数组
   * @return HashSet对象
   */
  @SafeVarargs
  public <T> Set<T> newSortedSet(T... ts) {
    return set(false, ts);
  }

  /**
   * 新建一个HashSet
   *
   * @param <T>      集合元素类型
   * @param isSorted 是否有序，有序返回 {@link java.util.LinkedHashSet}，否则返回 {@link java.util.HashSet}
   * @param ts       元素数组
   * @return HashSet对象
   */
  @SafeVarargs
  public static <T> Set<T> set(boolean isSorted, T... ts) {
    if (null == ts) {
      return isSorted ? new LinkedHashSet<>() : new HashSet<>();
    }
    int initialCapacity = Math.max((int) (ts.length / .75f) + 1, 16);
    final HashSet<T> set = isSorted ? new LinkedHashSet<>(initialCapacity) : new HashSet<>(initialCapacity);
    Collections.addAll(set, ts);
    return set;
  }
}
