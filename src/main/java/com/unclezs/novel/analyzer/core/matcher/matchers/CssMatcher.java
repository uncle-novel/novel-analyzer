package com.unclezs.novel.analyzer.core.matcher.matchers;

import com.unclezs.novel.analyzer.core.matcher.MatcherAlias;
import com.unclezs.novel.analyzer.core.rule.CommonRule;
import com.unclezs.novel.analyzer.model.Pair;
import com.unclezs.novel.analyzer.util.CollectionUtils;
import com.unclezs.novel.analyzer.util.StringUtils;
import com.unclezs.novel.analyzer.util.regex.RegexUtils;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.List;
import java.util.Set;

/**
 * 支持 @text @ownText  等其他attr
 *
 * @author blog.unclezs.com
 * @date 2020/12/21 14:08
 */
@Slf4j
public class CssMatcher extends Matcher {
  private static final CssMatcher ME = new CssMatcher();
  /**
   * 绝对路径  eg. abs:href   abs:src
   */
  private static final String ABS_URL = "abs:";
  /**
   * 支持的自定义属性选择器
   */
  private static final Set<String> SUPPORT_ATTR = CollectionUtils.newSet("href", "src", "text", "ownText");

  private CssMatcher() {
  }

  /**
   * 获取单例
   *
   * @return 实例
   */
  public static CssMatcher me() {
    return ME;
  }

  /**
   * 别名列表
   *
   * @return 别名列表
   */
  @Override
  public MatcherAlias[] aliases() {
    return new MatcherAlias[]{MatcherAlias.alias("css:"), MatcherAlias.alias("css")};
  }

  /**
   * 匹配列表
   *
   * @param src      源
   * @param listRule 列表规则
   * @param <E>      列表类型
   * @return /
   */
  @Override
  @SuppressWarnings("unchecked")
  protected <E> List<E> list(String src, CommonRule listRule) {
    Pair<String, String> css = getCss(listRule.getRule());
    return (List<E>) Jsoup.parse(src).select(css.getLeft());
  }

  /**
   * 匹配一个
   *
   * @param element 源文本
   * @param rule    规则
   * @return 匹配结果
   */
  @Override
  protected <E> String one(E element, String rule) {
    if (element instanceof String) {
      return match(StringUtils.toStringNullToEmpty(element), rule);
    }
    if (element instanceof Element) {
      return match((Element) element, rule);
    }
    return StringUtils.EMPTY;
  }

  /**
   * 匹配一个
   *
   * @param src  源文本
   * @param rule 规则
   * @return 匹配结果
   */
  public String match(String src, String rule) {
    return match(Jsoup.parse(src), rule);
  }


  /**
   * 正则匹配
   *
   * @param src      源
   * @param cssQuery 正则
   * @return /
   */
  public String match(Element src, String cssQuery) {
    Pair<String, String> css = getCss(cssQuery);
    return match(src, css.getLeft(), css.getRight());
  }

  /**
   * 正则匹配
   *
   * @param element  源
   * @param cssQuery 正则
   * @return /
   */
  public String match(Element element, String cssQuery, String attr) {
    // 判断是否有属性选择器，没有则默认返回text
    if (StringUtils.isEmpty(attr)) {
      return selectAllText(element, cssQuery, false);
    } else {
      switch (attr) {
        case "text":
          return selectAllText(element, cssQuery, false);
        case "ownText":
          return selectAllText(element, cssQuery, true);
        default:
          return element.select(cssQuery).attr(attr);
      }
    }
  }

  /**
   * 选中全部标签文本
   *
   * @param element  元素
   * @param cssQuery 选择器
   * @param isOwn    是否只选择自己的文本
   * @return 文本
   */
  private String selectAllText(Element element, String cssQuery, boolean isOwn) {
    Elements elements = element.select(cssQuery);
    if (CollectionUtils.isEmpty(elements)) {
      return StringUtils.EMPTY;
    }
    StringBuilder result = new StringBuilder();
    for (Element ele : elements) {
      result.append(isOwn ? ele.ownText() : ele.text());
      if (elements.size() > 1) {
        result.append(StringUtils.NEW_LINE);
      }
    }
    return result.toString();
  }


  /**
   * 是否支持的属性
   *
   * @param attr /
   * @return /
   */
  private boolean support(String attr) {
    return SUPPORT_ATTR.contains(attr) || attr.startsWith(ABS_URL) || RegexUtils.isWord(attr);
  }

  /**
   * 选择第一个
   *
   * @param document 文档
   * @param cssQuery cssQuery
   * @return /
   */
  private Element selectFirst(Element document, String cssQuery) {
    Elements elements = document.select(cssQuery);
    if (elements.isEmpty()) {
      return null;
    }
    return elements.get(0);
  }

  /**
   * 获取css  left-> Jsoup支持的css选择器  right-> 自定义的属性选择器
   *
   * @param cssQuery query
   * @return /
   */
  private Pair<String, String> getCss(String cssQuery) {
    int mid = cssQuery.lastIndexOf("@");
    Pair<String, String> css = new Pair<>();
    if (mid != -1) {
      String attr = cssQuery.substring(mid + 1);
      String cssSelector = cssQuery.substring(0, mid);
      // 不支持的标签不当做属性
      if (support(attr)) {
        css.setLeft(cssSelector);
        css.setRight(attr);
        return css;
      }
    }
    css.setLeft(cssQuery);
    return css;
  }
}
