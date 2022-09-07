package com.unclezs.novel.analyzer.core.matcher.matchers;

import com.unclezs.novel.analyzer.core.matcher.MatcherAlias;
import com.unclezs.novel.analyzer.core.rule.CommonRule;

import java.util.List;

/**
 *
 * @author blog.unclezs.com
 * @since 2021/01/21 19:02
 */
public interface Matcher {
  /**
   * 获取别名列表
   *
   * @return 别名列表
   */
  MatcherAlias[] aliases();

  /**
   * 匹配列表
   *
   * @param src      源
   * @param listRule 规则
   * @param <E>      类型
   * @return 列表结果
   */
  <E> List<E> list(String src, CommonRule listRule);

  /**
   * 匹配一个
   *
   * @param element 解析目标对象 可能是element,也可能是String
   * @param rule    规则
   * @return 解析结果
   */
  <E> String one(E element, String rule);
}
