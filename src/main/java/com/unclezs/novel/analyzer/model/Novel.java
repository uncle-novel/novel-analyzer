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
   * 小说所属站点
   */
  private String site;
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
   * 播音
   */
  private String broadcast;
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

  /**
   * 去除首尾空白
   */
  public void trim() {
    if (title != null) {
      this.title = StringUtils.trim(title);
    }
    if (author != null) {
      this.author = StringUtils.trim(author);
    }
    if (broadcast != null) {
      this.broadcast = StringUtils.trim(broadcast);
    }
    if (category != null) {
      this.category = StringUtils.trim(category);
    }
    if (wordCount != null) {
      this.wordCount = StringUtils.trim(wordCount);
    }
    if (introduce != null) {
      this.introduce = StringUtils.trim(introduce);
    }
    if (latestChapterName != null) {
      this.latestChapterName = StringUtils.trim(latestChapterName);
    }
    if (state != null) {
      this.state = StringUtils.trim(state);
    }
    if (updateTime != null) {
      this.updateTime = StringUtils.trim(updateTime);
    }
  }
}
