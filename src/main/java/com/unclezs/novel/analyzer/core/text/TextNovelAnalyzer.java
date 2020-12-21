package com.unclezs.novel.analyzer.core.text;

import com.unclezs.novel.analyzer.core.comparator.ChapterComparator;
import com.unclezs.novel.analyzer.core.model.Rule;
import com.unclezs.novel.analyzer.core.model.TextAnalyzerConfig;
import com.unclezs.novel.analyzer.spider.model.Chapter;
import com.unclezs.novel.analyzer.utils.CollectionUtil;
import com.unclezs.novel.analyzer.utils.StringUtil;
import com.unclezs.novel.analyzer.utils.UrlUtil;
import lombok.experimental.UtilityClass;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 文本小说解析器
 *
 * @author blog.unclezs.com
 * @date 2020/12/20 6:42 下午
 */
@UtilityClass
public class TextNovelAnalyzer {
  /**
   * 获取小说正文
   *
   * @param originalText html/json
   * @param config       解析配置
   * @return /
   */
  public String content(String originalText, TextAnalyzerConfig config) {
    // 自定义范围
    Rule rule = config.getRule();
    // 支持范围匹配 则进行范围截取
    if (rule.isSupportRange()) {
      originalText = StringUtil.getRange(config.getRangeHeader(), config.getRangeTail(), originalText);
    }
    String content = rule.matching(originalText);
    // 缩进处理 每段缩进4个空格
    content = StringUtil.indentation(content);
    // html空格处理
    content = StringUtil.htmlBlank(content);
    // ncr转中文
    if (config.isNcr()) {
      content = StringUtil.ncr2Chinese(content);
    }
    // 去广告
    if (CollectionUtil.isNotEmpty(config.getAdvertisements())) {
      content = StringUtil.remove(content, config.getAdvertisements().toArray(new String[0]));
    }
    return content;
  }

  /**
   * 获取小说章节列表
   *
   * @param originalText html/json
   * @param config       解析配置
   * @return /
   */
  public List<Chapter> chapters(String originalText, TextAnalyzerConfig config) {
    // 支持范围匹配 则进行范围截取
    if (config.getRule().isSupportRange()) {
      originalText = StringUtil.getRange(config.getRangeHeader(), config.getRangeTail(), originalText);
    }
    Document document = Jsoup.parse(originalText, config.getBaseUri());
    List<Element> elements = document.body().select("a");
    //章节过滤
    if (config.isChapterFilter()) {
      elements = filterImpuritiesLinks(elements);
    }
    Stream<Chapter> chapterStream = elements.stream()
        .map(a -> new Chapter(a.text(), a.absUrl("href")))
        // 去重
        .distinct();
    //乱序重排
    if (config.isChapterSort()) {
      chapterStream = chapterStream.sorted(new ChapterComparator());
    }
    return chapterStream.collect(Collectors.toList());
  }

  /**
   * 过滤URL
   * 找出节点所在dom树深度次数最多的a标签
   *
   * @param aTags a节点列表
   */
  private List<Element> filterImpuritiesLinks(List<Element> aTags) {
    final int depth = maxTimesKey(aTags, aTag -> aTag.parents().size());
    final int part = maxTimesKey(aTags, aTag -> aTag.absUrl("href").split("/").length);
    return aTags.stream().filter(tag -> {
      String href = tag.absUrl("href");
      return tag.parents().size() == depth && UrlUtil.notAnchor(href) && part == href.split("/").length
          && tag.hasText();
    }).collect(Collectors.toList());
  }

  /**
   * 找出出现次数最多的key  key在这里可以是
   * 1. <a>标签在哪一级出现次数最多  key=哪一级
   * 2. <a>标签的href属性 通过 / 分割，一共多少段，出现次数最多的段数=key
   * <p>
   * 相当于找出一些数字中出现次数最多的那个数
   *
   * @param elements 节点列表
   * @param keyFunc  key值计算器
   * @return 出现次数最多的key
   */
  private int maxTimesKey(List<Element> elements, ToIntFunction<Element> keyFunc) {
    //  时间复杂度 On
    Map<Integer, Integer> map = new HashMap<>(10);
    for (Element a : elements) {
      int key = keyFunc.applyAsInt(a);
      map.compute(key, (k, c) -> {
        if (c == null) {
          return 1;
        }
        return c + 1;
      });
    }
    // 出现次数最多的key的次数
    int keyMaxCount = Collections.max(map.values());
    // 找出key是谁
    for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
      if (entry.getValue() == keyMaxCount) {
        return entry.getKey();
      }
    }
    return 0;
  }
}
