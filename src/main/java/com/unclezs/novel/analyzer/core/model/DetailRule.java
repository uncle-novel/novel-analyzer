package com.unclezs.novel.analyzer.core.model;

import com.unclezs.novel.analyzer.core.rule.CommonRule;
import com.unclezs.novel.analyzer.model.Verifiable;
import com.unclezs.novel.analyzer.request.RequestParams;
import lombok.Data;

import java.io.Serializable;

/**
 * 小说信息规则
 *
 * @author blog.unclezs.com
 * @date 2021/2/6 22:03
 */
@Data
public class DetailRule implements Serializable, Verifiable {
  private static final long serialVersionUID = 6939110987532593242L;
  /**
   * 请求参数
   */
  private RequestParams params;
  /**
   * 小说目录地址
   */
  private CommonRule url;
  /**
   * 书名
   */
  private CommonRule title;
  /**
   * 作者
   */
  private CommonRule author;
  /**
   * 分类
   */
  private CommonRule category;
  /**
   * 字数
   */
  private CommonRule wordCount;
  /**
   * 简介
   */
  private CommonRule introduce;
  /**
   * 最新章节名称
   */
  private CommonRule latestChapterName;
  /**
   * 最新章节链接
   */
  private CommonRule latestChapterUrl;
  /**
   * 封面
   */
  private CommonRule coverUrl;
  /**
   * 状态 已完结/连载中
   */
  private CommonRule state;
  /**
   * 最近更新时间
   */
  private CommonRule updateTime;

  @Override
  public boolean isEffective() {
    return true;
  }
}
