package com.unclezs.novel.analyzer.spider;

import com.unclezs.novel.analyzer.core.NovelMatcher;
import com.unclezs.novel.analyzer.core.helper.AnalyzerHelper;
import com.unclezs.novel.analyzer.core.matcher.matchers.RegexMatcher;
import com.unclezs.novel.analyzer.core.model.AnalyzerRule;
import com.unclezs.novel.analyzer.core.model.ContentRule;
import com.unclezs.novel.analyzer.core.model.TocRule;
import com.unclezs.novel.analyzer.model.Chapter;
import com.unclezs.novel.analyzer.model.Novel;
import com.unclezs.novel.analyzer.request.RequestParams;
import com.unclezs.novel.analyzer.spider.helper.SpiderHelper;
import com.unclezs.novel.analyzer.util.CollectionUtils;
import com.unclezs.novel.analyzer.util.StringUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

/**
 * 小说爬虫
 *
 * @author blog.unclezs.com
 * @date 2020/12/20 6:15 下午
 */
@Slf4j
@Getter
@Setter
public class NovelSpider {
  /**
   * 解析配置
   */
  private AnalyzerRule rule;

  public NovelSpider() {
  }

  public NovelSpider(AnalyzerRule rule) {
    this.rule = rule;
  }

  /**
   * 获取小说正文
   *
   * @param url 正文链接
   * @throws IOException IO异常
   */
  public String content(String url) throws IOException {
    return content(RequestParams.create(url));
  }

  /**
   * 获取小说正文
   *
   * @param params 请求
   * @throws IOException IO异常
   */
  public String content(RequestParams params) throws IOException {
    return content(params, null).getData();
  }

  /**
   * 获取小说正文
   *
   * @param params 请求
   * @throws IOException IO异常
   */
  public Result<String> content(RequestParams params, Consumer<String> pageConsumer) throws IOException {
    ContentRule contentRule = getRule().getContent();
    String originalText = SpiderHelper.request(null, params);
    StringBuilder contentBuilder = new StringBuilder();
    String content = NovelMatcher.content(originalText, contentRule);
    contentBuilder.append(content);
    if (pageConsumer != null) {
      pageConsumer.accept(content);
    }
    // 记录已经访问过的页面
    Set<String> visited = CollectionUtils.set(false, params.getUrl());
    int page = 1;
    // 如果允许下一页
    if (contentRule != null && contentRule.isAllowNextPage()) {
      String uniqueId = RegexMatcher.me().titleWithoutNumber(originalText);
      params.setUrl(AnalyzerHelper.nextPage(originalText, contentRule.getNext(), params.getUrl()));
      while (visited.add(params.getUrl())) {
        originalText = SpiderHelper.request(contentRule.getContent(), params);
        // 判断是否符合本页为同一
        if (!uniqueId.equals(RegexMatcher.me().titleWithoutNumber(originalText))) {
          break;
        }
        String pageContent = NovelMatcher.content(originalText, contentRule);
        contentBuilder.append(StringUtils.LF).append(pageContent);
        // 回调
        if (pageConsumer != null) {
          pageConsumer.accept(pageContent);
        }
        page++;
        // 下一页
        params.setUrl(AnalyzerHelper.nextPage(originalText, contentRule.getNext(), params.getUrl()));
      }
    }
    log.debug("小说章节内容:{} 抓取完成，共{}页，共{}字", params.getUrl(), visited.size(), contentBuilder.length());
    return new Result<>(page, contentBuilder.toString());
  }

  /**
   * 获取小说章节列表
   *
   * @param url 目录地址
   * @throws IOException IO异常
   */
  public List<Chapter> toc(String url) throws IOException {
    return toc(RequestParams.create(url));
  }

  /**
   * 获取小说章节列表
   *
   * @param params 目录地址
   * @throws IOException IO异常
   */
  public List<Chapter> toc(RequestParams params) throws IOException {
    return toc(params, null);
  }

  /**
   * 获取小说章节列表
   *
   * @param params       目录地址
   * @param pageConsumer 单页抓取完成回调
   * @throws IOException IO异常
   */
  public List<Chapter> toc(RequestParams params, Consumer<List<Chapter>> pageConsumer) throws IOException {
    TocRule tocRule = getRule().getToc();
    String originalText = SpiderHelper.request(null, params);
    List<Chapter> toc = NovelMatcher.toc(originalText, tocRule);
    if (pageConsumer != null) {
      pageConsumer.accept(toc);
    }
    // 记录已经访问过的页面
    Set<String> visited = CollectionUtils.set(false, params.getUrl());
    // 如果允许下一页
    if (tocRule != null && tocRule.isAllowNextPage()) {
      String uniqueId = RegexMatcher.me().titleWithoutNumber(originalText);
      params.setUrl(AnalyzerHelper.nextPage(originalText, tocRule.getNext(), params.getUrl()));
      // 如果是新的链接则进行
      while (visited.add(params.getUrl())) {
        originalText = SpiderHelper.request(null, params);
        // 判断是否符合本页为同一
        if (!uniqueId.equals(RegexMatcher.me().titleWithoutNumber(originalText))) {
          break;
        }
        // 页面章节列表
        List<Chapter> pageChapters = NovelMatcher.toc(originalText, tocRule);
        if (CollectionUtils.isNotEmpty(pageChapters)) {
          toc.addAll(pageChapters);
          if (pageConsumer != null) {
            pageConsumer.accept(pageChapters);
          }
        }
        // 下一页
        params.setUrl(AnalyzerHelper.nextPage(originalText, tocRule.getNext(), params.getUrl()));
      }
    }
    toc = TocSpider.pretreatmentToc(toc, params.getUrl(), tocRule, 1);
    log.debug("小说目录:{} 抓取完成，共{}页，{}章节", params.getUrl(), visited.size(), toc.size());
    return toc;
  }

  /**
   * 小说详情
   *
   * @param params 详情页请求
   * @return 小说信息
   */
  public Novel details(RequestParams params) throws IOException {
    Novel novel = NovelMatcher.details(SpiderHelper.request(params), getRule().getDetail());
    // 对相对路径自动完整URL
    novel.competeUrl(params.getUrl());
    return novel;
  }
}
