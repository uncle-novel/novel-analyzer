package com.unclezs.novel.analyzer.spider.pipline;

import com.unclezs.novel.analyzer.core.helper.AnalyzerHelper;
import com.unclezs.novel.analyzer.model.Chapter;
import com.unclezs.novel.analyzer.util.StringUtils;
import lombok.Getter;
import lombok.Setter;

import java.nio.charset.StandardCharsets;

/**
 * 如果是文本内容 优先使用这个类当做基类
 * 文本小说管道
 * 包含了格式预处理
 *
 * @author blog.unclezs.com
 * @date 2021/02/08 10:45
 */
public abstract class AbstractTextPipeline extends BaseFilePipeline {
  @Setter
  @Getter
  private String charset = StandardCharsets.UTF_8.name();

  /**
   * 预处理
   *
   * @param chapter 数据
   */
  @Override
  public void process(Chapter chapter) {
    String content = chapter.getContent();
    // 预处理文本格式
    content = AnalyzerHelper.formatContent(content);
    // 标题加入正文， 如果标题已经包含了则移除
    content = chapter.getName() + StringUtils.LF + StringUtils.removeTitle(content, chapter.getName());
    chapter.setContent(content);
    processChapter(chapter);
  }

  /**
   * 子类处理已经预处理好的文本
   *
   * @param chapter 处理的章节
   */
  public abstract void processChapter(Chapter chapter);
}
