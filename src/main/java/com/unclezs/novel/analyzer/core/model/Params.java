package com.unclezs.novel.analyzer.core.model;

import lombok.Data;

import java.io.Serializable;

/**
 * 全局默认参数
 *
 * @author blog.unclezs.com
 * @date 2021/4/26 17:40
 */
@Data
public class Params implements Serializable {
  /**
   * 是否启动动态网页
   */
  private Boolean dynamic;
  /**
   * 启用代理
   */
  private Boolean enabledProxy;
  /**
   * 自定义Cookie
   */
  private String cookie;
  /**
   * 自定义UA
   */
  private String userAgent;
}
