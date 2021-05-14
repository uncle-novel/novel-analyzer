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
  protected abstract <E> List<E> list(String src, CommonRule listRule);

  /**
   * 匹配一个
   *
   * @param element 解析目标对象 可能是element,也可能是String
   * @param rule    规则
   * @return 解析结果
   */
  protected abstract <E> String one(E element, String rule);

  /**
   * 匹配一个
   *
   * @param source 源文本
   * @param rule   规则
   * @return 匹配结果
   */
  public String match(Object source, CommonRule rule) {
    if (CommonRule.isEffective(rule)) {
      String result;
      if (CommonRule.isEffectiveOnlyScript(rule)) {
        result = StringUtils.toStringNullToEmpty(source);
      } else {
        result = one(source, rule.getRule());
      }
      // 净化
      result = purify(rule.getReplace(), result);
      // 后置处理
      result = handleScript(StringUtils.toStringNullToEmpty(source), result, rule.getScript());
      if (StringUtils.isBlank(result)) {
        return null;
      }
      return result;
    }
    return null;
  }

  /**
   * 匹配一页里面的多个结果，并且封装到Map
   *
   * @param source  源节点（节点、字符串）
   * @param ruleMap 规则集合
   * @return 匹配结果
   */
  public Map<String, String> matchMultiple(Object source, Map<String, CommonRule> ruleMap) {
    // 获取要匹配的字段，放入Map key:字段名 value:Field
    Map<String, String> resultMap = new HashMap<>(ruleMap.size() * 2);
    for (Map.Entry<String, CommonRule> ruleEntry : ruleMap.entrySet()) {
      // 匹配单个结果
      String result = match(source, ruleEntry.getValue());
      // 设置结果
      resultMap.put(ruleEntry.getKey(), result);
    }
    return resultMap;
  }

  /**
   * 匹配一页里面的多个结果，并且封装到Bean
   *
   * @param source  源节点（节点、字符串）
   * @param ruleMap 规则集合
   * @param type    类型
   * @return 匹配结果
   */
  public <T> T matchMultiple(Object source, Map<String, CommonRule> ruleMap, Class<T> type) {
    return BeanUtils.mapToBean(matchMultiple(source, ruleMap), type);
  }

  /**
   * 匹配列表
   *
   * @param src        源
   * @param listRule   列表规则
   * @param childRules 列表元素匹配规则
   * @return 列表结果
   */
  public List<Map<String, String>> matchList(String src, CommonRule listRule, Map<String, CommonRule> childRules) {
    List<Map<String, String>> items = new ArrayList<>();
    matchList(src, listRule, element -> {
      try {
        items.add(matchMultiple(element, childRules));
      } catch (Exception error) {
        log.error("匹配列表过程中失败：{}", error.getMessage(), error);
      }
    });
    return items;
  }

  /**
   * 匹配列表
   *
   * @param src         源
   * @param listRule    列表规则
   * @param itemHandler 列表项目处理
   */
  public void matchList(String src, CommonRule listRule, Consumer<Object> itemHandler) {
    List<Object> list = list(src, listRule);
    // 处理每个子元素
    for (Object element : list) {
      itemHandler.accept(element);
    }
  }

  /**
   * 匹配列表
   *
   * @param src        源
   * @param listRule   列表规则
   * @param type       列表元素类型
   * @param childRules 列表元素匹配规则
   * @param <E>        类型
   * @return 列表结果
   */
  public <E> List<E> matchList(String src, CommonRule listRule, Map<String, CommonRule> childRules, Class<E> type) {
    List<Map<String, String>> resultsMap = matchList(src, listRule, childRules);
    List<E> items = new ArrayList<>();
    if (resultsMap != null) {
      for (Map<String, ?> map : resultsMap) {
        items.add(BeanUtils.mapToBean(map, type));
      }
    }
    return items;
  }

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
        // 包含模板则进行模板替换
        if (StringUtils.isNotBlank(rule.getTo()) && rule.getTo().contains("$")) {
          src = RegexUtils.replaceAll(src, rule.getFrom(), rule.getTo());
        } else {
          src = src.replaceAll(rule.getFrom(), rule.getTo());
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
  private String handleScript(String source, String result, String script) {
    // 脚本二次处理
    if (StringUtils.isNotBlank(script)) {
      // 脚本初始变量 添加 source 、result
      ScriptContext.put(ScriptContext.SCRIPT_CONTEXT_VAR_SOURCE, source);
      ScriptContext.put(ScriptContext.SCRIPT_CONTEXT_VAR_RESULT, result);
      result = ScriptUtils.execute(script, ScriptContext.current());
      // 如果没有数据 则移除上下文数据
      ScriptContext.remove(ScriptContext.SCRIPT_CONTEXT_VAR_RESULT, ScriptContext.SCRIPT_CONTEXT_VAR_SOURCE);
      ScriptContext.removeIfEmpty();
    }
    return result;
  }
}
