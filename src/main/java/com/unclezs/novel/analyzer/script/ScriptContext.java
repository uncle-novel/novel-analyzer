package com.unclezs.novel.analyzer.script;

import com.unclezs.novel.analyzer.common.concurrent.ThreadContext;
import lombok.experimental.UtilityClass;

import javax.script.Bindings;
import javax.script.SimpleBindings;
import java.util.Map;

/**
 * JS脚本 上下文
 * 用于多个方法间的设置JS的默认化变量
 * 1. 当前页面的地址  url
 * 2. 当前页面源码 source
 * 3. 当前页面解析后的结果 result
 *
 * @author blog.unclezs.com
 * @date 2021/2/3 0:06
 */
@UtilityClass
public class ScriptContext {
  /**
   * 当前页面的地址  url
   */
  public static final String SCRIPT_CONTEXT_VAR_URL = "url";
  /**
   * 当前页面源码 source
   */
  public static final String SCRIPT_CONTEXT_VAR_SOURCE = "source";
  /**
   * 当前页面解析后的结果 result
   */
  public static final String SCRIPT_CONTEXT_VAR_RESULT = "result";

  /**
   * 爬虫上下文
   */
  private static final ThreadContext CONTEXT = new ThreadContext();

  /**
   * 添加数据
   *
   * @param key   健
   * @param value 值
   */
  public static void put(String key, Object value) {
    CONTEXT.put(key, value);
  }

  /**
   * 当前线程上下文Map 移除一个数据
   *
   * @param keys 健列表
   */
  public static void remove(String... keys) {
    Map<String, Object> map = CONTEXT.get();
    if (map != null && keys != null) {
      for (String key : keys) {
        map.remove(key);
      }
    }
  }

  /**
   * 获取当前的上下文，只能获取一次
   * 用于脚本执行时获取
   *
   * @return 脚本初始变量
   */
  public static Bindings current() {
    Map<String, Object> map = CONTEXT.get();
    if (map != null) {
      return new SimpleBindings(CONTEXT.get());
    }
    return new SimpleBindings();
  }

  /**
   * 移除当前的的线程上下文
   */
  public static void remove() {
    CONTEXT.remove();
  }

  /**
   * 移除当前的
   */
  public static void removeIfEmpty() {
    Map<String, Object> map = CONTEXT.get();
    if (map != null && map.isEmpty()) {
      remove();
    }
  }

}
