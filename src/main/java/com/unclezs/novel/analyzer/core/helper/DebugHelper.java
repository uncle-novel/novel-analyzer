package com.unclezs.novel.analyzer.core.helper;

import com.unclezs.novel.analyzer.util.StringUtils;
import lombok.experimental.UtilityClass;
import org.slf4j.helpers.MessageFormatter;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

/**
 * 调试辅助类，用于解析器调试时的日志记录查看
 *
 * @author blog.unclezs.com
 * @date 2021/07/06
 */
@UtilityClass
public class DebugHelper {
  /**
   * 显示网页源码
   */
  public static boolean showSource = false;
  /**
   * 是否启用
   */
  public static boolean enabled = false;

  /**
   * 观察者
   */
  private static final Set<Consumer<String>> OBSERVERS = new HashSet<>();

  public static void debug(String pattern, Object... args) {
    if (!enabled()) {
      return;
    }
    String message = MessageFormatter.arrayFormat(pattern, args).getMessage();
    OBSERVERS.forEach(stringConsumer -> stringConsumer.accept(message + StringUtils.LF));
  }

  /**
   * 订阅debug日志信息
   *
   * @param consumer 消费者
   */
  public static void subscribe(Consumer<String> consumer) {
    OBSERVERS.add(consumer);
    enabled = !OBSERVERS.isEmpty();
  }

  /**
   * 取消订阅日志消费者
   *
   * @param consumer 消费者
   */
  public static void unsubscribe(Consumer<String> consumer) {
    OBSERVERS.remove(consumer);
    enabled = !OBSERVERS.isEmpty();
  }

  /**
   * 是否启用debug
   *
   * @return true 启用
   */
  public static boolean enabled() {
    return enabled && !OBSERVERS.isEmpty();
  }
}
