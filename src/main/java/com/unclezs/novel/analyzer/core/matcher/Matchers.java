package com.unclezs.novel.analyzer.core.matcher;

import com.unclezs.novel.analyzer.common.annotation.Alias;
import com.unclezs.novel.analyzer.core.matcher.matchers.Matcher;
import com.unclezs.novel.analyzer.core.matcher.matchers.RegexMatcher;
import com.unclezs.novel.analyzer.core.rule.CommonRule;
import com.unclezs.novel.analyzer.core.rule.ReplaceRule;
import com.unclezs.novel.analyzer.script.ScriptContext;
import com.unclezs.novel.analyzer.script.ScriptUtils;
import com.unclezs.novel.analyzer.util.BeanUtils;
import com.unclezs.novel.analyzer.util.CollectionUtils;
import com.unclezs.novel.analyzer.util.StringUtils;
import com.unclezs.novel.analyzer.util.regex.RegexUtils;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

/**
 * 匹配器  正则,xpath,jsonpath,css
 * <p>
 * 格式： 解析器类型：解析规则[##$正则组号(仅regex可选)]
 * <p>
 * eg: css:div -&gt; .author -&gt; a@href
 * <p>
 * regex:&lt;title&gt;(.+?)&lt;/title&gt;##$1
 *
 * @author blog.unclezs.com
 * @since 2020/12/21 11:24
 */
@Slf4j
@UtilityClass
public class Matchers {
  /**
   * 匹配 规则带有选择器标识 css:xxx
   *
   * @param src  源文档
   * @param rule 规则
   * @return /
   */
  public static String match(Object src, String rule) {
    CommonRule commonRule = CommonRule.create(rule);
    return match(src, commonRule);
  }

  /**
   * 匹配 规则带有选择器标识 css:xxx
   *
   * @param source 源节点（节点、字符串）
   * @param rule   通用规则
   * @return /
   */
  public String match(Object source, CommonRule rule) {
    String result = null;
    if (CommonRule.isEffective(rule)) {
      Matcher matcher = MatcherManager.getMatcher(rule.getType());
      if (matcher == null) {
        result = StringUtils.toStringNullToEmpty(source);
      } else {
        result = matcher.one(source, rule.getRule());
      }
      // 净化
      result = purify(rule.getReplace(), result);
      // 后置处理
      Object scriptResult = handleScript(StringUtils.toStringNullToEmpty(source), result, rule.getScript());
      result = StringUtils.toStringNullToEmpty(scriptResult);
    }
    return StringUtils.isBlank(result) ? null : result;
  }

  /**
   * 匹配一页里面的多个结果，并且封装到Map
   *
   * @param source  源节点（节点、字符串）
   * @param ruleMap 规则集合
   * @return 匹配结果
   */
  public Map<String, String> matchMultiple(Object source, Map<String, CommonRule> ruleMap) {
    // 设定了规则的则不改变，否则则使用map中第一个带规则type的type
    String ruleType = ruleMap.entrySet().stream()
      .filter(rule -> MatcherManager.getMatcher(rule.getValue().getType()) != null)
      .findFirst()
      .map(entry -> entry.getValue().getType())
      .orElse(null);
    // 默认规则类型
    if (StringUtils.isNotBlank(ruleType)) {
      ruleMap.entrySet().stream()
        .filter(rule -> MatcherManager.getMatcher(rule.getValue().getType()) == null)
        .forEach(entry -> entry.getValue().setType(ruleType));
    }
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
   * @param src  源
   * @param rule 列表规则
   * @return 匹配结果列表（可能是节点列表）
   */
  public static List<Object> matchList(String src, CommonRule rule) {
    List<Object> items = new ArrayList<>();
    matchList(src, rule, items::add);
    return items;
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
  @SuppressWarnings("unchecked")
  public void matchList(String src, CommonRule listRule, Consumer<Object> itemHandler) {
    List<Object> list = new ArrayList<>();
    // 列表匹配
    Matcher matcher = MatcherManager.getMatcher(listRule.getType());
    if (matcher != null) {
      list = matcher.list(src, listRule);
    }
    // 处理脚本
    if (StringUtils.isNotBlank(listRule.getScript())) {
      list = (List<Object>) handleScript(src, StringUtils.toStringNullToEmpty(list), listRule.getScript());
    }
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
   * 转化为Map,可以通过alias指定map的key
   *
   * @param obj         规则对象
   * @param ignore      true 忽略properties,false 包含properties，如果properties为空则此字段无效
   * @param properties  属性
   * @param defaultType 默认规则类型 为null时不设置
   * @return 规则Map
   * @see com.unclezs.novel.analyzer.common.annotation.Alias
   */
  public static Map<String, CommonRule> getChildMap(String defaultType, Object obj, boolean ignore, String... properties) {
    Class<?> clazz = obj.getClass();
    Map<String, CommonRule> map = new HashMap<>(16);
    Set<String> ignoreFieldSet = CollectionUtils.set(false, properties);
    BeanUtils.getFields(clazz).stream()
      .filter(f -> properties.length == 0 || ignore != ignoreFieldSet.contains(f.getName()))
      .forEach(field -> {
        try {
          // 选出类型为CommonRule且不为空的
          Object srcValue = field.get(obj);
          if (srcValue instanceof CommonRule) {
            CommonRule value = (CommonRule) srcValue;
            Alias alias = field.getAnnotation(Alias.class);
            // 规则没有指定类型 则设置为默认类型
            if (StringUtils.isNotBlank(defaultType) && StringUtils.isBlank(value.getType())) {
              value.setType(defaultType);
            }
            // 拼接完成后的完整规则，如果还无效则舍弃
            if (!CommonRule.isEffective(value)) {
              log.debug("无效规则：{}", value);
              return;
            }
            if (alias != null) {
              map.put(alias.value(), (CommonRule) srcValue);
            } else {
              map.put(field.getName(), (CommonRule) srcValue);
            }
          }
        } catch (IllegalAccessException e) {
          log.error("BeanUtils获取属性失败: {}", field.getName(), e);
        }
      });
    return map;
  }


  /**
   * 转化为Map,可以通过alias指定map的key
   *
   * @param obj  规则对象
   * @param type 规则类型
   * @return 规则Map
   * @see com.unclezs.novel.analyzer.common.annotation.Alias
   */
  public static Map<String, CommonRule> getChildMap(String type, Object obj) {
    return getChildMap(type, obj, true);
  }

  /**
   * 转化为Map,可以通过alias指定map的key
   *
   * @param obj 规则对象
   * @return 规则Map
   * @see com.unclezs.novel.analyzer.common.annotation.Alias
   */
  public static Map<String, CommonRule> getChildMap(Object obj) {
    return getChildMap(null, obj, true);
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
