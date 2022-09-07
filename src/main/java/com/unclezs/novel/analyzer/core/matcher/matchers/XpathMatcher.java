package com.unclezs.novel.analyzer.core.matcher.matchers;

import com.unclezs.novel.analyzer.core.matcher.MatcherAlias;
import com.unclezs.novel.analyzer.core.rule.CommonRule;
import com.unclezs.novel.analyzer.core.rule.RuleConstant;
import com.unclezs.novel.analyzer.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.seimicrawler.xpath.JXDocument;
import org.seimicrawler.xpath.JXNode;

import java.util.List;
import java.util.Objects;

/**
 * 开源地址：https://github.com/zhegexiaohuozi/JsoupXpath
 * 在线测试：浏览器插件 xpath helper
 *
 * @author blog.unclezs.com
 * @since 2020/12/21 16:20
 */
@Slf4j
public class XpathMatcher implements Matcher {
  private static final XpathMatcher ME = new XpathMatcher();

  private XpathMatcher() {
  }

  /**
   * 获取单例
   *
   * @return 实例
   */
  public static XpathMatcher me() {
    return ME;
  }

  /**
   * 别名列表
   *
   * @return 别名列表
   */
  @Override
  public MatcherAlias[] aliases() {
    return new MatcherAlias[]{MatcherAlias.alias(RuleConstant.TYPE_XPATH.concat(StringUtils.COLON)), MatcherAlias.alias(RuleConstant.TYPE_XPATH), MatcherAlias.defaultAlias("//")};
  }

  /**
   * 匹配列表
   *
   * @param src      源
   * @param listRule 规则
   * @param <E>      类型
   * @return 列表结果
   */
  @Override
  @SuppressWarnings("unchecked")
  public <E> List<E> list(String src, CommonRule listRule) {
    if (StringUtils.isBlank(src)) {
      return null;
    }
    List<JXNode> nodes = JXDocument.create(src).selN(listRule.getRule());
    return (List<E>) nodes;
  }

  /**
   * XPath匹配一个
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
    if (element instanceof JXDocument) {
      return match((JXDocument) element, rule);
    } else if (element instanceof JXNode) {
      return match((JXNode) element, rule);
    }
    return match(element.toString(), rule);
  }

  /**
   * XPath匹配一个
   *
   * @param src  源文本
   * @param rule 规则
   * @return 匹配结果
   */
  public String match(String src, String rule) {
    return match(JXDocument.create(src), rule);
  }

  /**
   * XPath匹配一个
   *
   * @param src  源文本
   * @param rule 规则
   * @return 匹配结果
   */
  public String match(JXDocument src, String rule) {
    List<JXNode> results = src.selN(rule);
    boolean selectText = rule.toLowerCase().endsWith("text()");
    if (results != null) {
      StringBuilder result = new StringBuilder();
      for (JXNode node : results) {
        if (node.isElement()) {
          result.append(node.asElement().text());
        } else {
          result.append(node);
        }
        // 不是选择文本则不进行追加
        if (!selectText) {
          break;
        }
        if (results.size() > 1) {
          result.append(StringUtils.NEW_LINE);
        }
      }
      return result.toString();
    }
    return StringUtils.EMPTY;
  }

  /**
   * XPath匹配一个
   *
   * @param src  源文本
   * @param rule 规则
   * @return 匹配结果
   */
  public String match(JXNode src, String rule) {
    JXNode ret = src.selOne(rule);
    if (ret != null) {
      return Objects.toString(ret);
    }
    return StringUtils.EMPTY;
  }
}
