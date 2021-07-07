package com.unclezs.novel.analyzer.spider;

import com.unclezs.novel.analyzer.common.page.AbstractPageable;
import com.unclezs.novel.analyzer.core.NovelMatcher;
import com.unclezs.novel.analyzer.core.comparator.ChapterComparator;
import com.unclezs.novel.analyzer.core.helper.AnalyzerHelper;
import com.unclezs.novel.analyzer.core.helper.DebugHelper;
import com.unclezs.novel.analyzer.core.matcher.matchers.RegexMatcher;
import com.unclezs.novel.analyzer.core.model.AnalyzerRule;
import com.unclezs.novel.analyzer.core.model.TocRule;
import com.unclezs.novel.analyzer.model.Chapter;
import com.unclezs.novel.analyzer.model.Novel;
import com.unclezs.novel.analyzer.request.RequestParams;
import com.unclezs.novel.analyzer.script.ScriptContext;
import com.unclezs.novel.analyzer.script.ScriptUtils;
import com.unclezs.novel.analyzer.spider.helper.SpiderHelper;
import com.unclezs.novel.analyzer.util.CollectionUtils;
import com.unclezs.novel.analyzer.util.RandomUtils;
import com.unclezs.novel.analyzer.util.StringUtils;
import com.unclezs.novel.analyzer.util.uri.UrlUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.script.SimpleBindings;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 小说目录爬虫
 *
 * @author blog.unclezs.com
 * @date 2021/2/12 22:13
 */
@Slf4j
@Getter
@Setter
@NoArgsConstructor
public class TocSpider extends AbstractPageable<Chapter> {
  /**
   * 章节顺序比较器
   */
  public static final ChapterComparator CHAPTER_COMPARATOR = new ChapterComparator();
  /**
   * 章节排序脚本的内置变量
   */
  public static final String COMPARATOR_A = "a";
  public static final String COMPARATOR_B = "b";
  /**
   * 规则
   */
  private AnalyzerRule rule;
  /**
   * 请求目录参数
   */
  private RequestParams params;
  /**
   * 小说目录唯一ID
   */
  private String uniqueId = "none";
  private int order = 1;
  /**
   * 小说详情
   */
  @Getter
  private Novel novel;

  public TocSpider(AnalyzerRule rule) {
    this.rule = rule;
    // 不忽略错误
    super.setIgnoreError(false);
  }

  /**
   * 预处理目录
   *
   * @param toc     目录
   * @param baseUrl BaseURL
   * @param tocRule 规则
   * @param order   起始序号
   * @return 预处理后的目录
   */
  public static List<Chapter> pretreatmentToc(List<Chapter> toc, String baseUrl, TocRule tocRule, int order) {
    if (CollectionUtils.isNotEmpty(toc)) {
      // 去重，并且移除javascript的链接
      toc = toc.stream()
        .distinct()
        .filter(chapter -> chapter.getUrl() != null && !chapter.getUrl().startsWith("javascript"))
        .collect(Collectors.toList());
      if (tocRule != null) {
        // 移除黑名单列表
        if (CollectionUtils.isNotEmpty(tocRule.getBlackUrls())) {
          List<Chapter> blackList = toc.stream()
            .filter(chapter -> tocRule.getBlackUrls().contains(chapter.getUrl()))
            .collect(Collectors.toList());
          toc.removeAll(blackList);
        }
        // 章节过滤
        if (Boolean.TRUE.equals(tocRule.getFilter())) {
          toc = AnalyzerHelper.filterImpuritiesChapters(toc);
        }
      }
      // 自动拼接完整URL
      toc.stream()
        .filter(chapter -> !UrlUtils.isHttpUrl(chapter.getUrl()))
        .forEach(chapter -> chapter.setUrl(UrlUtils.completeUrl(baseUrl, chapter.getUrl())));
      // 重排
      sortToc(tocRule, toc);
      // 逆序
      if (tocRule != null && Boolean.TRUE.equals(tocRule.getReverse())) {
        Collections.reverse(toc);
      }
      // 编号
      for (Chapter chapter : toc) {
        chapter.setOrder(order++);
      }
    }
    return toc;
  }

  /**
   * 章节排序
   *
   * @param tocRule 目录规则
   * @param toc     目录
   */
  public static void sortToc(TocRule tocRule, List<Chapter> toc) {
    if (tocRule == null || Boolean.FALSE.equals(tocRule.getSort())) {
      return;
    }
    // 脚本排序
    if (StringUtils.isNotBlank(tocRule.getSortScript())) {
      try {
        toc.sort((o1, o2) -> {
          SimpleBindings bindings = new SimpleBindings();
          bindings.put(COMPARATOR_A, o1);
          bindings.put(COMPARATOR_B, o2);
          return (int) Double.parseDouble(ScriptUtils.execute(tocRule.getSortScript(), bindings));
        });
      } catch (Exception e) {
        DebugHelper.debug("【章节】：排序失败");
        DebugHelper.debug("【章节】：失败原因：{}", e.getMessage());
      }
    } else {
      // 默认排序
      toc.sort(CHAPTER_COMPARATOR);
    }
  }

  /**
   * 获取小说目录
   *
   * @param url 目录地址
   * @throws IOException IO异常
   */
  public void toc(String url) throws IOException {
    this.order = 1;
    this.params = RequestParams.create(url, rule.getToc().getParams());
    super.firstLoad();
  }

  /**
   * 目录唯一ID
   *
   * @param item 数据项
   * @return 唯一标识
   */
  @Override
  protected String getUniqueId(Chapter item) {
    return item.getUrl();
  }

  /**
   * 加载一页目录
   *
   * @param page 下一页页码
   * @return true 还有更多
   * @throws IOException 网页请求异常
   */
  @Override
  protected boolean loadPage(int page) throws IOException {
    TocRule tocRule = getRule().getToc();
    // 获取网页内容
    String originalText = SpiderHelper.request(rule.getParams(), params);
    boolean hasMore = false;
    try {
      // 解析小说详情，从目录页
      if (page == 1) {
        this.novel = NovelMatcher.details(originalText, rule.getDetail());
        this.novel.setUrl(params.getUrl());
      }
      List<Chapter> chapters = NovelMatcher.toc(originalText, tocRule);
      // 预处理目录
      chapters = pretreatmentToc(chapters, params.getUrl(), tocRule, this.order);
      if (tocRule.isAllowNextPage()) {
        // 获取网页唯一ID 为 网页标题只留下了中文（不包含零到十）
        String pageUniqueId = RegexMatcher.me().titleWithoutNumber(originalText);
        String nextPageUrl = AnalyzerHelper.nextPage(originalText, tocRule.getNext(), params.getUrl());
        // 下一页存在 条件：下一页不为空 并且 唯一ID相等或者是第一页 并且 允许翻页
        hasMore = StringUtils.isNotBlank(nextPageUrl);
        // 智能解析模式 强校验唯一ID
        if (!tocRule.isEffective()) {
          hasMore = Objects.equals(pageUniqueId, this.uniqueId) || page == 1;
        }
        if (CollectionUtils.isNotEmpty(chapters)) {
          hasMore = addItems(chapters) && hasMore;
          log.trace("小说目录 第{}页 抓取完成，共{}章.", page, chapters.size());
        }
        if (hasMore) {
          this.uniqueId = pageUniqueId;
          this.params.setUrl(nextPageUrl);
        } else {
          // 已经抓取完成
          log.debug("小说目录:{} 抓取完成，共{}页.", params.getUrl(), page);
        }
      } else {
        addItems(chapters);
      }
    } finally {
      // 抓取完成移除上下文数据
      ScriptContext.remove();
    }
    if (StringUtils.isBlank(novel.getTitle())) {
      novel.setTitle("未知标题" + RandomUtils.randomInt(1000));
    }
    return hasMore;
  }

  /**
   * @return 下一页链接
   */
  public String getNextPageUrl() {
    return this.params.getUrl();
  }
}

