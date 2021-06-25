package com.unclezs.novel.analyzer.spider;

import com.github.houbb.opencc4j.util.ZhConverterUtil;
import com.unclezs.novel.analyzer.core.NovelMatcher;
import com.unclezs.novel.analyzer.core.helper.AnalyzerHelper;
import com.unclezs.novel.analyzer.core.matcher.matchers.RegexMatcher;
import com.unclezs.novel.analyzer.core.model.AnalyzerRule;
import com.unclezs.novel.analyzer.core.model.ContentRule;
import com.unclezs.novel.analyzer.core.model.DetailRule;
import com.unclezs.novel.analyzer.core.model.TocRule;
import com.unclezs.novel.analyzer.model.Chapter;
import com.unclezs.novel.analyzer.model.Novel;
import com.unclezs.novel.analyzer.request.RequestParams;
import com.unclezs.novel.analyzer.script.ScriptContext;
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
    return content(url, null).getData();
  }

  /**
   * 获取小说正文
   *
   * @param url 正文链接
   * @throws IOException IO异常
   */
  public Result<String> content(String url, Consumer<String> pageConsumer) throws IOException {
    ContentRule contentRule = getRule().getContent();
    // 请求参数
    RequestParams params = RequestParams.create(url, contentRule.getParams());

    StringBuilder contentBuilder = new StringBuilder();
    int page = 1;
    // 记录已经访问过的页面
    Set<String> visited = CollectionUtils.set(false, params.getUrl());
    String originalText = request(params);
    log.trace("获取到网页{}源码：{}", params.getUrl(), originalText);
    try {
      String content = NovelMatcher.content(originalText, contentRule);
      contentBuilder.append(content);
      if (pageConsumer != null) {
        pageConsumer.accept(content);
      }
      // 文本小说进行翻页
      if (Boolean.FALSE.equals(rule.getAudio()) && contentRule.isAllowNextPage()) {
        String uniqueId = RegexMatcher.me().titleWithoutNumber(originalText);
        params.setUrl(AnalyzerHelper.nextPage(originalText, contentRule.getNext(), params.getUrl()));
        while (visited.add(params.getUrl())) {
          originalText = request(params);
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
    } finally {
      ScriptContext.remove();
    }
    String result = contentBuilder.toString();
    // 繁体转简体
    if (Boolean.TRUE.equals(contentRule.getTraditionToSimple())) {
      result = ZhConverterUtil.toSimple(result);
    }
    log.trace("小说章节内容:{} 抓取完成，共{}页，共{}字", params.getUrl(), visited.size(), contentBuilder.length());
    return new Result<>(page, result);
  }

  /**
   * 获取小说章节列表
   *
   * @param url 目录地址
   * @throws IOException IO异常
   */
  public List<Chapter> toc(String url) throws IOException {
    return toc(url, null);
  }

  /**
   * 获取小说章节列表
   *
   * @param url          目录地址
   * @param pageConsumer 单页抓取完成回调
   * @throws IOException IO异常
   */
  public List<Chapter> toc(String url, Consumer<List<Chapter>> pageConsumer) throws IOException {
    TocRule tocRule = getRule().getToc();
    RequestParams params = RequestParams.create(url, tocRule.getParams());
    // 记录已经访问过的页面
    Set<String> visited = CollectionUtils.set(false, params.getUrl());
    List<Chapter> toc;
    String originalText = request(params);
    try {
      toc = NovelMatcher.toc(originalText, tocRule);
      if (pageConsumer != null) {
        pageConsumer.accept(toc);
      }
      // 如果允许下一页
      if (tocRule.isAllowNextPage()) {
        String uniqueId = RegexMatcher.me().titleWithoutNumber(originalText);
        params.setUrl(AnalyzerHelper.nextPage(originalText, tocRule.getNext(), params.getUrl()));
        // 如果是新的链接则进行
        while (visited.add(params.getUrl())) {
          originalText = request(params);
          // 判断是否符合本页为同一
          if (Boolean.FALSE.equals(tocRule.getForceNext()) && !uniqueId.equals(RegexMatcher.me().titleWithoutNumber(originalText))) {
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
    } finally {
      ScriptContext.remove();
    }
    log.debug("小说目录:{} 抓取完成，共{}页，{}章节", params.getUrl(), visited.size(), toc.size());
    return toc;
  }

  /**
   * 小说详情
   *
   * @param url 请求链接
   * @return 小说信息
   */
  public Novel details(String url) throws IOException {
    DetailRule detailRule = rule.getDetail();
    RequestParams params = RequestParams.create(url, detailRule.getParams());
    Novel novel;
    String originalText = request(params);
    try {
      novel = NovelMatcher.details(originalText, detailRule);
      // 对相对路径自动完整URL
      novel.competeUrl(params.getUrl());
      // 去除首尾空白
      novel.trim();
    } finally {
      ScriptContext.remove();
    }
    return novel;
  }

  private String request(RequestParams params) throws IOException {
    return SpiderHelper.request(rule.getParams(), params);
  }
}
