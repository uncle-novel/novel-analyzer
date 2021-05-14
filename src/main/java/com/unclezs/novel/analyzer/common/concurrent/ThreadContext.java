package com.unclezs.novel.analyzer.common.concurrent;

import java.util.HashMap;
import java.util.Map;

/**
 * 线程上下文 封装为Map
 *
 * @author blog.unclezs.com
 * @date 2021/2/2 23:00
 */
public class ThreadContext {
  private final ThreadLocal<Map<String, Object>> CONTEXT = new ThreadLocal<>();

  /**
   * 添加数据
   *
   * @param key   健
   * @param value 值
   */
  public void put(String key, Object value) {
    Map<String, Object> contextMap = get();
    contextMap.put(key, value);
  }

  /**
   * 获取数据
   *
   * @param key 健
   */
  public Object get(String key) {
    return get().get(key);
  }

  /**
   * 获取当前线程的上下文的Map
   *
   * @return 线程上下文Map
   */
  public Map<String, Object> get() {
    Map<String, Object> contextMap = CONTEXT.get();
    if (contextMap == null) {
      contextMap = new HashMap<>();
      CONTEXT.set(contextMap);
    }
    return contextMap;
  }

  /**
   * 移除当前的
   */
  public void remove() {
    CONTEXT.remove();
  }
}
