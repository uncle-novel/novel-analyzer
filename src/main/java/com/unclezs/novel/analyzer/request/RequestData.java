package com.unclezs.novel.analyzer.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * 请求数据
 *
 * @author blog.unclezs.com
 * @date 2020/12/20 5:51 下午
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestData {
  /**
   * 请求链接
   */
  private String url;
  /**
   * 是否为post请求
   */
  @Builder.Default
  private boolean post = false;
  /**
   * 网页编码
   */
  @Builder.Default
  private String charset = StandardCharsets.UTF_8.toString();
  /**
   * 请求头
   */
  private Map<String, String> headers;
  /**
   * 请求方式
   */
  @Builder.Default
  private String mediaType = MediaType.NONE.getMediaType();
  /**
   * 请求体
   */
  private String body;

}
