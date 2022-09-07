package com.unclezs.novel.analyzer.spider;

import com.github.houbb.opencc4j.util.ZhConverterUtil;
import com.unclezs.novel.analyzer.core.NovelMatcher;
import com.unclezs.novel.analyzer.core.helper.AnalyzerHelper;
import com.unclezs.novel.analyzer.core.helper.DebugHelper;
import com.unclezs.novel.analyzer.core.matcher.matchers.RegexMatcher;
import com.unclezs.novel.analyzer.core.model.AnalyzerRule;
import com.unclezs.novel.analyzer.core.model.ContentRule;
import com.unclezs.novel.analyzer.core.model.DetailRule;
import com.unclezs.novel.analyzer.core.model.TocRule;
import com.unclezs.novel.analyzer.core.rule.CommonRule;
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
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

/**
 * 小说爬虫
 *
 * @author blog.unclezs.com
 * @since 2020/12/20 6:15 下午
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
    boolean audio = Boolean.TRUE.equals(getRule().getAudio());
    // 如果为有声规则，且正文规则无效则直接返回URL（此处特殊逻辑处理了有声小说在目录解析时就获取到了真实音频地址的情况）
    String preScript = Optional.of(contentRule).map(ContentRule::getParams).map(RequestParams::getScript).orElse(null);
    if (audio && StringUtils.isBlank(preScript) && !CommonRule.isEffective(contentRule.getContent())) {
      DebugHelper.debug("【正文】：有声小说规则 并且 预处理脚本为空 并且 规则无效，不再请求正文链接：{}，匹配结果为正文链接", url);
      doConsumer(pageConsumer, url);
      return new Result<>(1, url);
    }
    // 请求参数
    RequestParams params = RequestParams.create(url, contentRule.getParams());
    StringBuilder contentBuilder = new StringBuilder();
    int page = 1;
    // 记录已经访问过的页面
    Set<String> visited = CollectionUtils.set(false, params.getUrl());
    String originalText = request(params);
    // 请求完成后，规则无效直接返回源码
    if (!CommonRule.isEffective(contentRule.getContent())) {
      DebugHelper.debug("【正文】：正文规则无效，匹配结果直接为 {} 的源码", url);
      doConsumer(pageConsumer, originalText);
      return new Result<>(1, originalText);
    }
    try {
      String content = NovelMatcher.content(originalText, contentRule);
      contentBuilder.append(content);
      if (pageConsumer != null) {
        pageConsumer.accept(content);
      }
      // 文本小说进行翻页
      if (Boolean.FALSE.equals(rule.getAudio()) && contentRule.isAllowNextPage()) {
        DebugHelper.debug("【正文】：正文翻页已启用");
        String uniqueId = RegexMatcher.me().titleWithoutNumber(originalText);
        params.setUrl(AnalyzerHelper.nextPage(originalText, contentRule.getNext(), params.getUrl()));
        while (visited.add(params.getUrl())) {
          originalText = request(params);
          // 判断是否符合本页为同一
          String currentUniqueId = RegexMatcher.me().titleWithoutNumber(originalText);
          if (!uniqueId.equals(currentUniqueId)) {
            DebugHelper.debug("【正文】：网页标题不匹配，停止翻页：正确的：{} , 不匹配的：{}", uniqueId, currentUniqueId);
            break;
          }
          String pageContent = NovelMatcher.content(originalText, contentRule);
          contentBuilder.append(StringUtils.LF).append(pageContent);
          // 回调
          doConsumer(pageConsumer, pageContent);
          page++;
          // 下一页
          params.setUrl(AnalyzerHelper.nextPage(originalText, contentRule.getNext(), params.getUrl()));
          DebugHelper.debug("【正文】：获取到下一页链接：{}，当前页码：{}", params.getUrl(), page);
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
    int order = 1;
    try {
      toc = NovelMatcher.toc(originalText, tocRule);
      toc = TocSpider.pretreatmentToc(toc, params.getUrl(), tocRule, order);
      order = toc.size();
      doConsumer(pageConsumer, toc);
      // 如果允许下一页
      boolean forceNext = Boolean.TRUE.equals(tocRule.getForceNext());
      DebugHelper.debug("【目录】：启用目录翻页：{}，强制翻页：{}", tocRule.isAllowNextPage(), forceNext);
      if (tocRule.isAllowNextPage()) {
        String uniqueId = RegexMatcher.me().titleWithoutNumber(originalText);
        params.setUrl(AnalyzerHelper.nextPage(originalText, tocRule.getNext(), params.getUrl()));
        // 如果是新的链接则进行
        while (visited.add(params.getUrl())) {
          originalText = request(params);
          // 判断是否符合本页为同一
          String currentUniqueId = RegexMatcher.me().titleWithoutNumber(originalText);
          if (!forceNext && !uniqueId.equals(currentUniqueId)) {
            DebugHelper.debug("【目录】：网页标题不匹配，且未开启强制翻页，停止翻页：正确的：{} , 不匹配的：{}", uniqueId, currentUniqueId);
            break;
          }
          // 页面章节列表
          List<Chapter> pageChapters = NovelMatcher.toc(originalText, tocRule);
          if (CollectionUtils.isNotEmpty(pageChapters)) {
            toc.addAll(pageChapters);
            toc = TocSpider.pretreatmentToc(pageChapters, params.getUrl(), tocRule, order);
            order = toc.size();
            doConsumer(pageConsumer, pageChapters);
          }
          // 下一页
          params.setUrl(AnalyzerHelper.nextPage(originalText, tocRule.getNext(), params.getUrl()));
          DebugHelper.debug("【目录】：获取到下一页链接：{}，当前页码：{}", params.getUrl(), visited.size());
        }
      }
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

  private <T> void doConsumer(Consumer<T> consumer, T param) {
    if (consumer != null) {
      consumer.accept(param);
    }
  }
}
