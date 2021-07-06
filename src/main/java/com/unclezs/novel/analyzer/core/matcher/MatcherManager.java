package com.unclezs.novel.analyzer.core.matcher;

import com.unclezs.novel.analyzer.core.matcher.matchers.CssMatcher;
import com.unclezs.novel.analyzer.core.matcher.matchers.DefaultTextMatcher;
import com.unclezs.novel.analyzer.core.matcher.matchers.JsonMatcher;
import com.unclezs.novel.analyzer.core.matcher.matchers.Matcher;
import com.unclezs.novel.analyzer.core.matcher.matchers.RegexMatcher;
import com.unclezs.novel.analyzer.core.matcher.matchers.XpathMatcher;
import com.unclezs.novel.analyzer.util.StringUtils;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * 匹配器管理器
 * 注意：xpath: 应该在xpath之前添加
 *
 * @author blog.unclezs.com
 * @date 2021/01/28 14:48
 */
public class MatcherManager {
  /**
   * 存放已经被注册的匹配器
   */
  private static final Map<String, Matcher> SUPPORT_MATCHER = new LinkedHashMap<>();
  /**
   * 默认的别名集合
   */
  private static final Set<String> DEFAULT_ALIAS_SET = new HashSet<>();

  static {
    // 正则
    registerMatcher(RegexMatcher.me());
    // xpath
    registerMatcher(XpathMatcher.me());
    // css选择器
    registerMatcher(CssMatcher.me());
    // json
    registerMatcher(JsonMatcher.me());
    // 默认的自带匹配器
    registerMatcher(DefaultTextMatcher.me());
  }

  /**
   * 注册匹配器
   *
   * @param matcher 匹配器
   */
  public static void registerMatcher(Matcher matcher) {
    registerMatcher(matcher, matcher.aliases());
  }

  /**
   * 注册匹配器
   *
   * @param matcher 匹配器
   * @param aliases 别名
   */
  public static void registerMatcher(Matcher matcher, String... aliases) {
    for (String alias : aliases) {
      SUPPORT_MATCHER.put(alias, matcher);
    }
  }

  /**
   * 注册匹配器
   *
   * @param matcher 匹配器
   * @param aliases 别名
   */
  public static void registerMatcher(Matcher matcher, MatcherAlias... aliases) {
    if (aliases == null) {
      return;
    }
    for (MatcherAlias alias : aliases) {
      if (alias.isDefault()) {
        DEFAULT_ALIAS_SET.add(alias.getName());
      }
      SUPPORT_MATCHER.put(alias.getName(), matcher);
    }
  }

  /**
   * 根据别名获取匹配器
   *
   * @param alias 别名
   */
  public static Matcher getMatcher(String alias) {
    if (StringUtils.isBlank(alias)) {
      return null;
    }
    return SUPPORT_MATCHER.get(alias);
  }

  /**
   * 根据别名获取匹配器
   *
   * @param alias   别名
   * @param matcher 未获取到返回
   */
  public static Matcher getMatcher(String alias, Matcher matcher) {
    return SUPPORT_MATCHER.getOrDefault(alias, matcher);
  }

  /**
   * 获取全部
   */
  public static Map<String, Matcher> all() {
    return Collections.unmodifiableMap(SUPPORT_MATCHER);
  }

  /**
   * 获取全部别名
   */
  public static Set<String> aliased() {
    return SUPPORT_MATCHER.keySet();
  }

  /**
   * 获取默认的别名集合
   *
   * @return 默认别名集合
   */
  public static Set<String> defaultAlias() {
    return Collections.unmodifiableSet(DEFAULT_ALIAS_SET);
  }

  /**
   * 判断是否为默认别名
   *
   * @param alias 别名
   * @return 是否为默认别名 true 是默认  null 则返回false
   */
  public static boolean isDefaultAlias(String alias) {
    return DEFAULT_ALIAS_SET.contains(alias);
  }
}
