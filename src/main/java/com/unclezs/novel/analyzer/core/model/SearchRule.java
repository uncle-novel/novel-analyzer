package com.unclezs.novel.analyzer.core.model;

import com.unclezs.novel.analyzer.core.rule.CommonRule;
import com.unclezs.novel.analyzer.model.Verifiable;
import com.unclezs.novel.analyzer.request.RequestParams;
import lombok.Data;

import java.io.Serializable;

/**
 * 搜索配置
 * <pre>
 *     # 最小配置
 *     "search":{
 *          "params":{
 *              "url": "搜索地址"
 *          },
 *          "detailPage": ""
 *          "detail":{
 *              "url": "小说目录地址规则"
 *              "title": "标题规则"
 *          },
 *          "list": "搜索结果列表",
 *     }
 * </pre>
 *
 * @author blog.unclezs.com
 * @date 2021/02/08 19:21
 */
@Data
public class SearchRule implements Serializable, Verifiable {
  private static final long serialVersionUID = 5277637191594804618L;
  /**
   * 搜索请求参数
   */
  private RequestParams params;
  /**
   * 结果列表
   */
  private CommonRule list;
  /**
   * 详情页规则
   */
  private CommonRule detailPage;
  /**
   * 小说详情
   */
  private DetailRule detail;

  /**
   * 是否为有效配置
   *
   * @return true有效
   */
  @Override
  public boolean isEffective() {
    return CommonRule.isEffective(list)
      && params != null && params.isEffective()
      && detail != null && CommonRule.hasRule(detail.getTitle()) && CommonRule.hasRule(detail.getUrl());
  }
}
