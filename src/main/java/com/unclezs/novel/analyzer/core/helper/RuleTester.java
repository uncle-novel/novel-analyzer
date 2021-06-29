package com.unclezs.novel.analyzer.core.helper;

import com.unclezs.novel.analyzer.core.model.AnalyzerRule;
import com.unclezs.novel.analyzer.core.model.ContentRule;
import com.unclezs.novel.analyzer.core.model.TocRule;
import com.unclezs.novel.analyzer.core.rule.CommonRule;
import com.unclezs.novel.analyzer.model.Chapter;
import com.unclezs.novel.analyzer.model.Novel;
import com.unclezs.novel.analyzer.model.Verifiable;
import com.unclezs.novel.analyzer.spider.NovelSpider;
import com.unclezs.novel.analyzer.spider.SearchSpider;
import com.unclezs.novel.analyzer.util.CollectionUtils;
import com.unclezs.novel.analyzer.util.GsonUtils;
import com.unclezs.novel.analyzer.util.RandomUtils;
import com.unclezs.novel.analyzer.util.StringUtils;
import com.unclezs.novel.analyzer.util.uri.UrlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static com.unclezs.novel.analyzer.util.StringUtils.LF;

/**
 * 规则测试器
 *
 * @author blog.unclezs.com
 * @date 2021/4/23 1:49
 */
@Slf4j
public class RuleTester {
  public static final String LINE = "============================";
  private static final String NEW_LINE = LINE.concat(LF).concat(LINE).concat(LF);
  private final AnalyzerRule rule;
  private final NovelSpider spider;
  private Consumer<String> messageConsumer = System.out::print;

  public RuleTester(AnalyzerRule rule) {
    this(rule, null);
  }

  public RuleTester(AnalyzerRule rule, Consumer<String> messageConsumer) {
    this.rule = rule;
    this.spider = new NovelSpider(rule);
    if (messageConsumer != null) {
      this.messageConsumer = messageConsumer;
    }
  }

  /**
   * 测试全部规则
   *
   * @param keyword 搜索关键词
   */
  public void test(String keyword) {
    // 测试搜索
    List<Novel> novels = search(keyword);
    if (!novels.isEmpty()) {
      // 取中间的一本小说测试
      Novel novel = RandomUtils.randomEle(novels);
      String tocUrl = novel.getUrl();
      messageConsumer.accept(NEW_LINE);
      messageConsumer.accept(String.format("选择第%d本小说测试目录解析:", novels.indexOf(novel) + 1).concat(LF));
      printNovel(novel);
      if (UrlUtils.isHttpUrl(tocUrl)) {
        // 测试目录解析
        List<Chapter> toc = toc(tocUrl);
        if (!toc.isEmpty()) {
          // 去第一章节测试
          Chapter chapter = RandomUtils.randomEle(toc);
          messageConsumer.accept(NEW_LINE);
          messageConsumer.accept(String.format("选择第%d章测试正文及详情解析:", toc.indexOf(chapter) + 1).concat(LF));
          String url = chapter.getUrl();
          // 测试正文
          content(url);
          // 测试详情
          detail(url);
        }
      }
    }
    printFooter("全部");
  }

  /**
   * 测试正文规则
   *
   * @param url 正文链接
   */
  public void content(String url) {
    String type = "正文解析";
    try {
      ContentRule contentRule = rule.getContent();
      printHeader(type, contentRule);
      AtomicInteger page = new AtomicInteger(0);
      spider.content(url, str -> {
        printPage(type, page.incrementAndGet());
        messageConsumer.accept(str + LF);
      });
      printFooter(type, page.get());
    } catch (Exception e) {
      printError(type, e);
    }
  }

  /**
   * 测试目录规则
   *
   * @param url 目录链接
   */
  public List<Chapter> toc(String url) {
    String type = "目录解析";
    try {
      TocRule tocRule = rule.getToc();
      printHeader(type, tocRule);
      if (tocRule == null || !tocRule.isEffective()) {
        messageConsumer.accept("目录规则无效，采用自动解析模式!!");
      }
      AtomicInteger page = new AtomicInteger(0);
      List<Chapter> toc = new ArrayList<>();
      spider.toc(url, chapters -> {
        if (CollectionUtils.isNotEmpty(chapters)) {
          printPage(type, page.incrementAndGet());
          StringJoiner chapterJoiner = new StringJoiner(LF);
          chapters.forEach(chapter -> {
            // 生成完整URL
            if (StringUtils.isNotBlank(chapter.getUrl())) {
              chapter.setUrl(UrlUtils.completeUrl(url, chapter.getUrl()));
            }
            chapterJoiner
              .add("名称：" + chapter.getName())
              .add("链接：" + chapter.getUrl())
              .add(LINE);
          });
          messageConsumer.accept(chapterJoiner.toString().concat(LF));
        }
        toc.addAll(chapters);
      });
      printFooter(type, page.get());
      return toc;
    } catch (Exception e) {
      printError(type, e);
    }
    return Collections.emptyList();
  }

  /**
   * 测试搜索规则
   *
   * @param keyword 关键词
   */
  public List<Novel> search(String keyword) {
    String type = "搜索";
    try {
      printHeader(type, rule.getSearch());
      List<Novel> novelList = new ArrayList<>();
      SearchSpider searchSpider = new SearchSpider(Collections.singletonList(rule));
      // 调试模式
      searchSpider.setDebug(true);
      AtomicInteger page = new AtomicInteger(-1);
      searchSpider.setOnNewItemAddHandler(novel -> {
        if (page.get() != searchSpider.getPage()) {
          page.set(searchSpider.getPage());
          printPage(type, page.get());
        }
        printNovel(novel);
        novelList.add(novel);
      });
      searchSpider.search(keyword);
      searchSpider.loadAll();
      printFooter(type, page.get());
      return novelList;
    } catch (Exception e) {
      printError(type, e);
    }
    return Collections.emptyList();
  }

  /**
   * 测试详情规则
   *
   * @param url 详情页链接
   */
  public void detail(String url) {
    String type = "详情解析";
    try {
      printHeader(type, rule.getDetail());
      Novel novel = spider.details(url);
      printNovel(novel);
      printFooter(type);
    } catch (Exception e) {
      printError(type, e);
    }
  }

  /**
   * 输出页码信息
   *
   * @param type 类型
   * @param page 页码
   */
  private void printPage(String type, int page) {
    StringJoiner pageRecorder = new StringJoiner(LF)
      .add(LF)
      .add(LINE)
      .add(String.format("%s第%d页完成", type, page))
      .add(LINE)
      .add(LF);
    messageConsumer.accept(pageRecorder.toString());
  }

  /**
   * 输出头部信息
   *
   * @param type 类型
   * @param rule 规则
   */
  private void printHeader(String type, Verifiable rule) {
    StringJoiner recorder = new StringJoiner(LF)
      .add(LINE)
      .add(String.format("%s测试开始：", type))
      .add("规则：")
      .add(GsonUtils.PRETTY.toJson(rule))
      .add(LF);
    messageConsumer.accept(recorder.toString());
  }

  /**
   * 输出结束信息
   *
   * @param type 类型
   */
  private void printFooter(String type) {
    printFooter(type, -1);
  }

  /**
   * 输出结束信息
   *
   * @param type 类型
   */
  private void printFooter(String type, int page) {
    String message = type + "测试已经完成！！";
    if (page > 0) {
      message = message + String.format(", 共%s页", page);
    }
    StringJoiner recorder = new StringJoiner(LF)
      .add(LINE)
      .add(message)
      .add(LINE)
      .add(LF);
    messageConsumer.accept(recorder.toString());
  }

  /**
   * 输出错误信息
   *
   * @param type 类型
   */
  private void printError(String type, Exception error) {
    StringJoiner recorder = new StringJoiner(LF)
      .add(LINE)
      .add(String.format("%s测试出现错误：", type))
      .add(ExceptionUtils.getStackTrace(error))
      .add(LINE)
      .add(LF);
    error.printStackTrace();
    messageConsumer.accept(recorder.toString());
  }


  /**
   * 输出小说信息
   */
  private void printNovel(Novel novel) {
    if (novel == null) {
      return;
    }
    StringJoiner novelJoiner = new StringJoiner(LF)
      .add("书名：" + novel.getTitle())
      .add("作者：" + novel.getAuthor())
      .add("播音：" + novel.getBroadcast())
      .add("链接：" + novel.getUrl())
      .add("分类：" + novel.getCategory())
      .add("封面：" + novel.getCoverUrl())
      .add("介绍：" + novel.getIntroduce())
      .add("最新章节名称：" + novel.getLatestChapterName())
      .add("字数：" + novel.getWordCount())
      .add("更新状态：" + novel.getState())
      .add("更新时间：" + novel.getUpdateTime())
      .add(LINE.concat(LF));
    messageConsumer.accept(novelJoiner.toString());
  }
}
