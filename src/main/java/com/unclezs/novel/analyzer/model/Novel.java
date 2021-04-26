package com.unclezs.novel.analyzer.model;

import com.unclezs.novel.analyzer.core.helper.AnalyzerHelper;
import com.unclezs.novel.analyzer.util.StringUtils;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * @author blog.unclezs.com
 * @date 2020/12/23 18:42
 */
@Data
public class Novel implements Serializable {
  /**
   * 目录链接
   */
  private String url;
  /**
   * 书名
   */
  private String title;
  /**
   * 作者
   */
  private String author;
  /**
   * 分类
   */
  private String category;
  /**
   * 字数
   */
  private String wordCount;
  /**
   * 简介
   */
  private String introduce;
  /**
   * 最新章节名称
   */
  private String latestChapterName;
  /**
   * 最新章节链接
   */
  private String latestChapterUrl;
  /**
   * 封面
   */
  private String coverUrl;
  /**
   * 状态
   */
  private String state;
  /**
   * 最近更新时间
   */
  private String updateTime;
  /**
   * 章节列表
   */
  private transient List<Chapter> chapters;

  /**
   * 拼接URL到完整路径
   *
   * @param baseUrl 基准URL
   */
  public void competeUrl(String baseUrl) {
    AnalyzerHelper.completeUrl(baseUrl, this::getCoverUrl, this::setCoverUrl);
    AnalyzerHelper.completeUrl(baseUrl, this::getUrl, this::setUrl);
    AnalyzerHelper.completeUrl(baseUrl, this::getLatestChapterUrl, this::setLatestChapterUrl);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Novel novel = (Novel) o;
    return Objects.equals(url, novel.url);
  }

  @Override
  public int hashCode() {
    return Objects.hash(url);
  }
}
