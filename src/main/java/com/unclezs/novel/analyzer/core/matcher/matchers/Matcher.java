package com.unclezs.novel.analyzer.core.matcher.matchers;

import com.unclezs.novel.analyzer.core.matcher.MatcherAlias;
import com.unclezs.novel.analyzer.core.rule.CommonRule;
import com.unclezs.novel.analyzer.core.rule.ReplaceRule;
import com.unclezs.novel.analyzer.script.ScriptContext;
import com.unclezs.novel.analyzer.script.ScriptUtils;
import com.unclezs.novel.analyzer.util.BeanUtils;
import com.unclezs.novel.analyzer.util.CollectionUtils;
import com.unclezs.novel.analyzer.util.StringUtils;
import com.unclezs.novel.analyzer.util.regex.RegexUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

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

  /**
   * 净化
   *
   * @param rules 净化规则
   * @param src   净化内容
   * @return 净化后结果
   */
  private String purify(Set<ReplaceRule> rules, String src) {
    if (CollectionUtils.isNotEmpty(rules)) {
      for (ReplaceRule rule : rules) {
        if (rule.getFrom().startsWith(RegexMatcher.REGEX_PREFIX)) {
          String from = rule.getFrom().substring(RegexMatcher.REGEX_PREFIX.length());
          // 包含模板则进行模板替换
          if (StringUtils.isNotBlank(rule.getTo()) && rule.getTo().contains("$")) {
            src = RegexUtils.replaceAll(src, from, rule.getTo());
          } else {
            src = src.replaceAll(from, rule.getTo());
          }
        } else {
          src = src.replace(rule.getFrom(), rule.getTo());
        }
      }
    }
    return src;
  }

  /**
   * 脚本对匹配结果进行二次处理
   *
   * @param source 源文本
   * @param result 匹配后的结果
   * @param script 脚本
   * @return 处理后的结果
   */
  private Object handleScript(String source, String result, String script) {
    Object ret = result;
    // 脚本二次处理
    if (StringUtils.isNotBlank(script)) {
      // 脚本初始变量 添加 source 、result
      ScriptContext.put(ScriptContext.VAR_SOURCE, source);
      ScriptContext.put(ScriptContext.VAR_RESULT, result);
      ret = ScriptUtils.executeForResult(script, ScriptContext.current());
      // 如果没有数据 则移除上下文数据
      ScriptContext.remove(ScriptContext.VAR_RESULT, ScriptContext.VAR_SOURCE);
      ScriptContext.removeIfEmpty();
    }
    return ret;
  }
}
