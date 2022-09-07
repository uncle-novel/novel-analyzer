package com.unclezs.novel.analyzer.script;

import com.unclezs.novel.analyzer.common.concurrent.ThreadContext;
import com.unclezs.novel.analyzer.util.CollectionUtils;
import lombok.experimental.UtilityClass;

import javax.script.Bindings;
import javax.script.SimpleBindings;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * JS脚本 上下文
 * 用于多个方法间的设置JS的默认化变量
 *
 * @author blog.unclezs.com
 * @since 2021/2/3 0:06
 */
@UtilityClass
public class ScriptContext {
  /**
   * 当前页面的地址  url
   */
  public static final String VAR_URL = "url";
  /**
   * 当前页面源码 source
   */
  public static final String VAR_SOURCE = "source";
  /**
   * 当前页面解析后的结果 result
   */
  public static final String VAR_RESULT = "result";
  /**
   * 全局参数
   */
  public static final String VAR_PARAMS = "params";
  /**
   * webview时的窗口变量
   */
  private static final Set<String> VARIABLES = CollectionUtils.newSet(VAR_PARAMS, VAR_URL, VAR_SOURCE, VAR_RESULT);

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
      Map<String, Object> vars = new HashMap<>(map);
      for (String key : map.keySet()) {
        if (!VARIABLES.contains(key)) {
          vars.remove(key);
        }
      }
      return new SimpleBindings(vars);
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
