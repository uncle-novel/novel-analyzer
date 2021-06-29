package com.unclezs.novel.analyzer.core.rule;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.unclezs.novel.analyzer.core.helper.RuleHelper;
import com.unclezs.novel.analyzer.core.matcher.MatcherManager;
import com.unclezs.novel.analyzer.model.Pair;
import com.unclezs.novel.analyzer.util.CollectionUtils;
import com.unclezs.novel.analyzer.util.GsonUtils;
import com.unclezs.novel.analyzer.util.StringUtils;
import lombok.Data;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 通用规则
 *
 * <pre>
 *    # 最小规则
 *    rule: "xpath://dic[@class='xx']"
 *    # 完整规则
 *    rule:{
 *      "type": "xx"
 *      "rule": "xpath://dic[@class='xx']"
 *      "script": "js脚本内容"
 *      "replace": [
 *         {
 *            "from":"正则"
 *            "to":"模板 eg: $1 取第一组"
 *         }
 *      ]
 *   }
 *   # 替换规则支持简写
 *   # 多个替换规则
 *   "replace": [
 *      {
 *         "from":"正则"
 *         "to":"模板 eg: $1 取第一组"
 *      }
 *   ]
 *   # 单个替换规则
 *   "replace":{
 *       "from":"正则"
 *       "to":"模板 eg: $1 取第一组"
 *   }
 *   # 单个替换规则 缩写一行
 *   "replace": "from##to"
 * </pre>
 *
 * @author blog.unclezs.com
 * @date 2021/1/27 21:54
 * @see com.unclezs.novel.analyzer.core.helper.RuleHelper
 */
@Data
public class CommonRule implements Serializable, JsonDeserializer<CommonRule>, JsonSerializer<CommonRule> {
  private static final long serialVersionUID = 6752071448939164168L;
  /**
   * 规则匹配的页面 比如 detail：详情页  search：搜索页
   */
  private String page;
  /**
   * 匹配器类型 可以手动填写这个，如果不填写则从规则进行解析
   */
  private String type;
  /**
   * 必填项，规则，支持带类型 如 xpath://dic[@class='xx']，不带类型则需要填type
   */
  private String rule;
  /**
   * 匹配到之后的后置处理脚本
   */
  private String script;
  /**
   * 净化规则
   */
  private Set<ReplaceRule> replace;

  /**
   * 是否为有效的规则
   */
  public static boolean isEffective(CommonRule commonRule) {
    return commonRule != null && (commonRule.isEffective() || StringUtils.isNotBlank(commonRule.getScript()));
  }

  /**
   * 是否为有效的规则
   */
  public static boolean isEffective(CommonRule... commonRules) {
    if (commonRules == null) {
      return false;
    }
    for (CommonRule commonRule : commonRules) {
      if (!isEffective(commonRule)) {
        return false;
      }
    }
    return true;
  }

  /**
   * 是否为有效的规则 只有脚本存在
   */
  public static boolean isEffectiveOnlyScript(CommonRule commonRule) {
    return commonRule != null && !commonRule.isEffective() && StringUtils.isNotBlank(commonRule.getScript());
  }

  /**
   * 是否包含Rule规则
   */
  public static boolean hasRule(CommonRule commonRule) {
    return commonRule != null && StringUtils.isNotBlank(commonRule.getRule());
  }

  /**
   * 创建一个规则
   *
   * @return 只包含规则类型与规则的
   */
  public static CommonRule create(String type, String rule) {
    CommonRule commonRule = new CommonRule();
    commonRule.setRule(rule);
    commonRule.setType(type);
    return commonRule;
  }

  /**
   * 创建一个规则
   *
   * @param withTypeRule 带类型的规则
   * @return 只包含规则类型与规则的
   */
  public static CommonRule create(String withTypeRule) {
    Pair<String, String> ruleType = RuleHelper.parseRuleType(withTypeRule);
    CommonRule commonRule = new CommonRule();
    commonRule.setType(ruleType.getLeft());
    commonRule.setRule(ruleType.getRight());
    return commonRule;
  }

  /**
   * 返回规则简写
   *
   * @param rule 规则
   * @return 简写规则
   */
  public static Supplier<String> ruleStringGetter(CommonRule rule) {
    return () -> {
      if (rule == null) {
        return null;
      }
      return rule.ruleString();
    };
  }

  /**
   * 设置规则简写
   *
   * @param rule 规则
   * @return 简写规则
   */
  public static Consumer<String> ruleStringSetter(CommonRule rule) {
    return ruleString -> {
      if (rule != null) {
        rule.setRuleString(ruleString);
      }
    };
  }

  /**
   * 添加净化规则
   *
   * @param rule 净化规则
   */
  public void addReplaceRule(ReplaceRule rule) {
    if (this.replace == null) {
      this.replace = new HashSet<>();
    }
    this.replace.remove(rule);
    this.replace.add(rule);
  }

  /**
   * 是否为有效的规则
   */
  public boolean isEffective() {
    return StringUtils.isNotBlank(type) && StringUtils.isNotBlank(rule);
  }

  /**
   * 转为rule字符串
   *
   * @return 字符串
   */
  public String ruleString() {
    if (StringUtils.isBlank(type) || (rule != null && rule.startsWith(type))) {
      return rule;
    }
    StringBuilder result = new StringBuilder();
    result.append(type);
    if (!type.endsWith(":") && !MatcherManager.isDefaultAlias(type)) {
      result.append(":");
    }
    if (StringUtils.isNotBlank(rule)) {
      result.append(rule);
    }
    return result.toString();
  }

  /**
   * 设置规则字符串
   *
   * @param ruleString 规则字符串
   */
  public void setRuleString(String ruleString) {
    Pair<String, String> pair = RuleHelper.parseRuleType(ruleString);
    if (pair != null) {
      this.type = pair.getLeft();
      this.rule = pair.getRight();
    } else {
      this.rule = ruleString;
      this.type = null;
    }
  }

  /**
   * 反序列化规则
   *
   * @param json    json节点
   * @param typeOfT 类型
   * @param context 上下文
   * @return 解析结果CommonRule
   * @throws JsonParseException /
   */
  @Override
  public CommonRule deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
    // 直接跳过
    if (typeOfT != CommonRule.class) {
      return null;
    }
    CommonRule commonRule = new CommonRule();
    // 规则是个对象
    if (json.isJsonObject()) {
      JsonObject ruleJson = json.getAsJsonObject();
      // 规则类型
      commonRule.setPage(GsonUtils.getOrDefault(ruleJson, "page", null));
      // 规则类型
      commonRule.setType(GsonUtils.getOrDefault(ruleJson, "type", null));
      // 实际规则
      commonRule.setRule(GsonUtils.getOrDefault(ruleJson, "rule", null));
      // 处理脚本
      commonRule.setScript(GsonUtils.getOrDefault(ruleJson, "script", null));
      // 净化
      JsonElement purifyRulesJsonElement = ruleJson.get("replace");
      if (purifyRulesJsonElement != null) {
        commonRule.setReplace(handlePurifyRule(purifyRulesJsonElement, context));
      }
    } else {
      // 字符串 则直接是规则
      String ruleValue = json.getAsString();
      if (StringUtils.isNotEmpty(ruleValue)) {
        commonRule.setRule(ruleValue);
      }
    }
    // 规则预处理  xpath://xx  分离
    if (StringUtils.isNotEmpty(commonRule.getRule())) {
      Pair<String, String> rulePair = RuleHelper.parseRuleType(commonRule.getRule());
      if (rulePair != null) {
        // 类型
        if (StringUtils.isNotEmpty(rulePair.getLeft()) && StringUtils.isEmpty(commonRule.getType())) {
          commonRule.setType(rulePair.getLeft());
        }
        // 实际规则
        if (StringUtils.isNotEmpty(rulePair.getRight())) {
          commonRule.setRule(rulePair.getRight());
        }
      }
    }
    return commonRule;
  }

  /**
   * 处理净化规则
   *
   * @param purifyRulesJsonElement 净化规则JSON
   * @param context                上下文
   * @return 净化规则列表
   */
  private Set<ReplaceRule> handlePurifyRule(JsonElement purifyRulesJsonElement, JsonDeserializationContext context) {
    Set<ReplaceRule> replaceRules = new HashSet<>();
    // 只有一个 可以为json对象
    if (purifyRulesJsonElement.isJsonObject()) {
      ReplaceRule replaceRule = context.deserialize(purifyRulesJsonElement, ReplaceRule.class);
      if (replaceRule != null) {
        replaceRules.add(replaceRule);
      }
      // 多个净化可以为数组
    } else if (purifyRulesJsonElement.isJsonArray()) {
      JsonArray purifyRulesJsonElementAsJsonArray = purifyRulesJsonElement.getAsJsonArray();
      for (JsonElement element : purifyRulesJsonElementAsJsonArray) {
        // 是JSON对象的方式
        if (element.isJsonObject()) {
          ReplaceRule replaceRule = context.deserialize(element, ReplaceRule.class);
          if (replaceRule != null) {
            replaceRules.add(replaceRule);
          }
        } else {
          // 直接为字符串 广告##模板
          String purifyRuleStr = element.getAsString();
          if (StringUtils.isNotEmpty(purifyRuleStr)) {
            replaceRules.add(ReplaceRule.parseRule(purifyRuleStr));
          }
        }
      }
    } else {
      // 直接为字符串 广告##模板
      String purifyRuleStr = purifyRulesJsonElement.getAsString();
      if (StringUtils.isNotEmpty(purifyRuleStr)) {
        replaceRules.add(ReplaceRule.parseRule(purifyRuleStr));
      }
    }
    return replaceRules;
  }

  @Override
  public JsonElement serialize(CommonRule commonRule, Type type, JsonSerializationContext jsonSerializationContext) {
    boolean hasReplace = CollectionUtils.isNotEmpty(commonRule.replace);
    boolean hasScript = StringUtils.isNotBlank(commonRule.script);
    boolean hasPage = StringUtils.isNotBlank(commonRule.page);
    JsonObject jsonObject = new JsonObject();
    // 只有规则
    if (!hasPage && !hasReplace && !hasScript) {
      String ruleString = commonRule.ruleString();
      if (ruleString == null) {
        return null;
      }
      return new JsonPrimitive(commonRule.ruleString());
    }
    if (StringUtils.isNotBlank(commonRule.ruleString())) {
      jsonObject.addProperty("rule", commonRule.ruleString());
    }
    if (hasReplace && commonRule.replace.size() == 1) {
      jsonObject.addProperty("replace", RuleHelper.GSON.toJson(commonRule.replace.iterator().next()));
    } else if (hasReplace) {
      jsonObject.add("replace", RuleHelper.GSON.toJsonTree(commonRule.replace));
    }
    if (hasPage) {
      jsonObject.addProperty("page", commonRule.page);
    }
    if (hasScript) {
      jsonObject.addProperty("script", commonRule.script);
    }
    return jsonObject;
  }
}
