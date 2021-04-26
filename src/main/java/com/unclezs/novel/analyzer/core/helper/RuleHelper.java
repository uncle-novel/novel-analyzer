package com.unclezs.novel.analyzer.core.helper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.unclezs.novel.analyzer.core.matcher.MatcherManager;
import com.unclezs.novel.analyzer.core.model.AnalyzerRule;
import com.unclezs.novel.analyzer.core.model.ContentRule;
import com.unclezs.novel.analyzer.core.rule.CommonRule;
import com.unclezs.novel.analyzer.core.rule.ReplaceRule;
import com.unclezs.novel.analyzer.model.Pair;
import com.unclezs.novel.analyzer.util.BeanUtils;
import com.unclezs.novel.analyzer.util.uri.UrlUtils;
import lombok.Setter;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * 规则辅助器
 *
 * @author blog.unclezs.com
 * @date 2021/1/28 8:30
 */
@Slf4j
@UtilityClass
public class RuleHelper {
  /**
   * 规则独特的gson解析器
   */
  public static final Gson GSON = new GsonBuilder()
    .registerTypeAdapter(CommonRule.class, new CommonRule())
    .registerTypeAdapter(ReplaceRule.class, new ReplaceRule())
    .registerTypeAdapter(ContentRule.class, new ContentRule())
    .create();
  /**
   * 所有规则
   */
  private static final Map<String, AnalyzerRule> RULES = new HashMap<>();
  /**
   * 规则改变监听
   */
  @Setter
  private static Consumer<List<AnalyzerRule>> onRuleChangeListener;

  /**
   * 获取规则
   *
   * @param url 网址
   * @return 规则
   */
  public static AnalyzerRule getRule(String url) {
    String host = UrlUtils.getHost(url);
    return RULES.get(host);
  }

  /**
   * 获取规则
   *
   * @param url 网站
   * @return 规则不存在则创建
   */
  public static AnalyzerRule getOrDefault(String url) {
    AnalyzerRule rule = getRule(url);
    if (rule == null) {
      rule = new AnalyzerRule();
      rule.setSite(UrlUtils.getSite(url));
    }
    return rule;
  }

  /**
   * 获取规则，不存在则使用默认，并存下来
   *
   * @param url 网址
   * @return 规则
   */
  public static AnalyzerRule rule(String url) {
    AnalyzerRule rule = getRule(url);
    if (rule == null) {
      rule = new AnalyzerRule();
      String host = UrlUtils.getHost(url);
      rule.setSite(url);
      rule.setName(host);
      addRule(rule);
    }
    return rule;
  }

  /**
   * 获取所有规则
   *
   * @return 规则
   */
  public static List<AnalyzerRule> rules() {
    return new ArrayList<>(RULES.values());
  }

  /**
   * 获取规则
   *
   * @param ruleJson 规则JSON
   * @return 本次加载了多少个规则
   */
  public static int loadRules(String ruleJson) {
    List<AnalyzerRule> rules = parseRules(ruleJson, AnalyzerRule.class);
    return setRules(rules);
  }

  /**
   * 获取规则
   *
   * @param rules 规则列表
   * @return 本次加载了多少个规则
   */
  public static int setRules(List<AnalyzerRule> rules) {
    RULES.clear();
    RuleHelper.addRule(rules);
    return rules.size();
  }

  /**
   * 添加规则，触发监听
   *
   * @param rule 规则
   */
  public static void addRule(AnalyzerRule rule) {
    addRule(Collections.singletonList(rule));
  }

  /**
   * 添加规则，触发监听
   *
   * @param rules 规则
   */
  public static void addRule(List<AnalyzerRule> rules) {
    rules.forEach(rule -> RULES.put(UrlUtils.getHost(rule.getSite()), rule));
    if (onRuleChangeListener != null) {
      onRuleChangeListener.accept(rules());
    }
  }

  /**
   * 设置规则的匹配器类型
   * 设置字段中的commonRule字段的type
   *
   * @param type    规则类型
   * @param ruleObj 规则对象
   */
  public static void setRuleType(String type, Object ruleObj) {
    BeanUtils.getFields(ruleObj.getClass()).stream()
      .filter(field -> field.getType().isAssignableFrom(CommonRule.class))
      .forEach(field -> {
        try {
          CommonRule rule = (CommonRule) field.get(ruleObj);
          if (rule != null) {
            rule.setType(type);
          }
        } catch (IllegalAccessException e) {
          log.error("设置规则类型失败: field:{} - type:{}", field.getName(), type, e);
        }
      });
  }

  /**
   * 解析JSON 返回规则列表
   *
   * @param ruleJson 规则JSON
   * @param clazz    类型
   * @param <T>      类型
   * @return 解析规则列表
   */
  public static <T> List<T> parseRules(String ruleJson, Class<T> clazz) {
    return GSON.fromJson(ruleJson, new ParameterizedTypeImpl<>(clazz));
  }

  /**
   * 解析JSON 返回规则列表
   *
   * @param ruleJson 规则JSON
   * @param clazz    类型
   * @param <T>      类型
   * @return 解析规则列表
   */
  public static <T> T parseRule(String ruleJson, Class<T> clazz) {
    return GSON.fromJson(ruleJson, clazz);
  }

  /**
   * 解析 单行的规则
   * xpath://xx/xx[@class='xx']
   *
   * @param rule left:type right:rule
   * @return 规则
   */
  public static Pair<String, String> parseRuleType(String rule) {
    if (rule == null) {
      return null;
    }
    Pair<String, String> rulePair = null;
    for (String alias : MatcherManager.aliased()) {
      if (rule.startsWith(alias)) {
        rulePair = new Pair<>();
        rulePair.setLeft(alias);
        // 非默认别名 则移除别名
        if (!MatcherManager.isDefaultAlias(alias)) {
          rulePair.setRight(rule.substring(alias.length()));
        } else {
          rulePair.setRight(rule);
        }
        break;
      }
    }
    return rulePair;
  }

  /**
   * 解析列表
   *
   * @param <T> /
   */
  private static class ParameterizedTypeImpl<T> implements ParameterizedType {
    Class<T> clazz;

    public ParameterizedTypeImpl(Class<T> clz) {
      clazz = clz;
    }

    @Override
    public Type[] getActualTypeArguments() {
      return new Type[]{clazz};
    }

    @Override
    public Type getRawType() {
      return List.class;
    }

    @Override
    public Type getOwnerType() {
      return null;
    }
  }
}
