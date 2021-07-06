package com.unclezs.novel.analyzer.core.matcher.matchers;

import com.unclezs.novel.analyzer.core.matcher.MatcherAlias;
import com.unclezs.novel.analyzer.core.rule.CommonRule;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 采用模板方法设计模式，解耦每个通用规则都需要调用 净化及后置处理
 *
 * @author blog.unclezs.com
 * @date 2021/01/21 19:02
 */
@Slf4j
public abstract class Matcher {
  /**
   * 获取别名列表
   *
   * @return 别名列表
   */
  public abstract MatcherAlias[] aliases();

  /**
   * 匹配列表
   *
   * @param src      源
   * @param listRule 规则
   * @param <E>      类型
   * @return 列表结果
   */
  public abstract <E> List<E> list(String src, CommonRule listRule);

  /**
   * 匹配一个
   *
   * @param element 解析目标对象 可能是element,也可能是String
   * @param rule    规则
   * @return 解析结果
   */
  public abstract <E> String one(E element, String rule);
}
