package com.unclezs.novel.analyzer.core.rule;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.unclezs.novel.analyzer.core.matcher.matchers.RegexMatcher;
import com.unclezs.novel.analyzer.model.Pair;
import com.unclezs.novel.analyzer.util.GsonUtils;
import com.unclezs.novel.analyzer.util.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Objects;

/**
 * 净化规则
 * <pre>
 *     # 最小规则
 *     replace: "正则##模版"
 *     # 完整规则
 *     replace: {
 *          "from":"正则匹配"
 *          "to": "替换模板"
 *     }
 * </pre>
 *
 * @author blog.unclezs.com
 * @date 2021/1/27 21:56
 * @see com.unclezs.novel.analyzer.core.helper.RuleHelper
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReplaceRule implements Serializable, JsonDeserializer<ReplaceRule>, JsonSerializer<ReplaceRule> {
  private static final long serialVersionUID = 830391532687585985L;
  /**
   * 广告匹配正则
   */
  private String from;
  /**
   * 替换模板，$1代表第一组 类推
   */
  private String to;

  /**
   * 获取净化规则
   *
   * @param replaceRules 净化规则
   * @return PurifyRule
   */
  public static ReplaceRule getRule(Object replaceRules) {
    @SuppressWarnings("unchecked")
    Map<String, Object> purify = (Map<String, Object>) replaceRules;
    return getRule(purify);
  }

  /**
   * 获取净化规则
   *
   * @param purify 净化规则
   * @return PurifyRule
   */
  public static ReplaceRule getRule(Map<String, Object> purify) {
    String regex = StringUtils.toString(purify.get("from"));
    if (StringUtils.isNotEmpty(regex)) {
      String template = StringUtils.toString(purify.get("to"));
      return new ReplaceRule(regex, template);
    }
    return null;
  }

  /**
   * 解析净化模板
   *
   * @param rule 规则 广告正则@模板
   * @return 净化规则
   */
  public static ReplaceRule parseRule(String rule) {
    ReplaceRule replaceRule = new ReplaceRule();
    Pair<String, String> pair = RegexMatcher.getTemplate(rule);
    replaceRule.setFrom(pair.getLeft());
    replaceRule.setTo("$0".equals(pair.getRight()) ? "" : pair.getRight());
    return replaceRule;
  }

  @Override
  public ReplaceRule deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
    // 一个对象
    if (json.isJsonObject()) {
      ReplaceRule replaceRule = new ReplaceRule();
      JsonObject ruleObject = json.getAsJsonObject();
      replaceRule.setFrom(GsonUtils.getOrDefault(ruleObject, "from", null));
      replaceRule.setTo(GsonUtils.getOrDefault(ruleObject, "to", null));
      return replaceRule;
    }
    return null;
  }

  @Override
  public JsonElement serialize(ReplaceRule rule, Type type, JsonSerializationContext jsonSerializationContext) {
    if (StringUtils.isBlank(rule.getFrom())) {
      return null;
    }
    return new JsonPrimitive(rule.getFrom() + RegexMatcher.REGEX_TEMPLATE_DELIMITER + rule.getTo());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ReplaceRule that = (ReplaceRule) o;
    return Objects.equals(from, that.from);
  }

  @Override
  public int hashCode() {
    return Objects.hash(from);
  }
}
