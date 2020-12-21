package com.unclezs.novel.analyzer.core.model;

import lombok.Data;

import java.util.List;

/**
 * @author blog.unclezs.com
 * @date 2020/12/20 6:49 下午
 */
@Data
public class TextAnalyzerConfig {
  /**
   * BaseUrI
   */
  private String baseUri;
  /**
   * 是否NRC转中文
   */
  private boolean ncr;
  /**
   * 广告字符串列表 可以是正则
   */
  private List<String> advertisements;
  /**
   * 范围头
   */
  private String rangeHeader;
  /**
   * 范围尾
   */
  private String rangeTail;
  /**
   * 规则
   */
  private Rule rule = Rule.TEXT_TAG;

  /**
   * 下一页正则（存在则会匹配下一页）
   */
  private String nextPage;

  /**
   * 章节乱序重排
   */
  private boolean chapterSort;
  /**
   * 章节过滤
   */
  private boolean chapterFilter;
}
