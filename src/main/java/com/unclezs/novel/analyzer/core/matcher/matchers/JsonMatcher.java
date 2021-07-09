package com.unclezs.novel.analyzer.core.matcher.matchers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.PathNotFoundException;
import com.jayway.jsonpath.spi.json.GsonJsonProvider;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.mapper.GsonMappingProvider;
import com.jayway.jsonpath.spi.mapper.MappingProvider;
import com.unclezs.novel.analyzer.core.matcher.MatcherAlias;
import com.unclezs.novel.analyzer.core.rule.CommonRule;
import com.unclezs.novel.analyzer.core.rule.RuleConstant;
import com.unclezs.novel.analyzer.util.GsonUtils;
import com.unclezs.novel.analyzer.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * JsonPath匹配器
 * 开源地址：https://github.com/json-path/JsonPath
 * 在线测试：http://jsonpath.herokuapp.com/
 *
 * @author blog.unclezs.com
 * @date 2020/12/21 16:20
 */
@Slf4j
public class JsonMatcher implements Matcher {
  private static final JsonMatcher ME = new JsonMatcher();

  static {
    // 使用gson
    GsonJsonProvider gsonJsonProvider = new GsonJsonProvider();
    MappingProvider mappingProvider = new GsonMappingProvider();
    Configuration.setDefaults(new Configuration.Defaults() {
      @Override
      public JsonProvider jsonProvider() {
        return gsonJsonProvider;
      }

      @Override
      public Set<Option> options() {
        return EnumSet.noneOf(Option.class);
      }

      @Override
      public MappingProvider mappingProvider() {
        return mappingProvider;
      }
    });
  }

  private JsonMatcher() {
  }

  /**
   * 获取单例
   *
   * @return 实例
   */
  public static JsonMatcher me() {
    return ME;
  }

  /**
   * 别名列表
   *
   * @return 别名列表
   */
  @Override
  public MatcherAlias[] aliases() {
    return new MatcherAlias[]{MatcherAlias.alias(RuleConstant.TYPE_JSON), MatcherAlias.alias(RuleConstant.TYPE_JSON.concat(StringUtils.COLON)), MatcherAlias.defaultAlias("$.")};
  }

  /**
   * 匹配列表
   *
   * @param src      源
   * @param listRule rule
   * @param <E>      类型
   * @return 列表结果
   */
  @Override
  @SuppressWarnings("unchecked")
  public <E> List<E> list(String src, CommonRule listRule) {
    if (StringUtils.isBlank(src)) {
      return new ArrayList<>();
    }
    JsonArray matchedList = JsonPath.parse(src).read(listRule.getRule());
    List<JsonElement> items = new ArrayList<>();
    for (JsonElement element : matchedList) {
      items.add(element);
    }
    return (List<E>) items;
  }

  /**
   * 匹配一个
   *
   * @param element 源文本
   * @param rule    规则
   * @return 匹配结果
   */
  @Override
  public <E> String one(E element, String rule) {
    if (element == null) {
      return null;
    }
    if (element instanceof String) {
      return match((String) element, rule);
    }
    return match(GsonUtils.toJson(element), rule);
  }

  /**
   * 匹配一个
   *
   * @param src  源文本
   * @param rule 规则
   * @return 匹配结果
   */
  public String match(String src, String rule) {
    try {
      src = StringUtils.removeQuote(src);
      Object ret = JsonPath.parse(src).read(rule);
      if (ret != null) {
        return StringUtils.removeQuote(ret.toString());
      }
    } catch (PathNotFoundException e) {
      log.trace("JsonPath未匹配到：{}", e.getMessage());
      return StringUtils.EMPTY;
    }
    return StringUtils.EMPTY;
  }

}
