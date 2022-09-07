package com.unclezs.novel.analyzer.request.spi;

import com.unclezs.novel.analyzer.request.RequestParams;

import java.io.IOException;

/**
 * @author blog.unclezs.com
 * @since 2020/12/24 11:09
 */
public interface HttpProvider {
  /**
   * 获取网页内容
   *
   * @param requestParams 请求参数
   * @return 结果字符
   * @throws IOException IO错误
   */
  String content(RequestParams requestParams) throws IOException;

  /**
   * 获取流
   *
   * @param requestParams 请求参数
   * @return 结果字节数组
   * @throws IOException IO错误
   */
  default byte[] bytes(RequestParams requestParams) throws IOException {
    return new byte[0];
  }


  /**
   * 检测链接是否能够连接成功
   *
   * @param requestParams 请求参数
   * @return true 能够
   */
  default boolean validate(RequestParams requestParams) throws IOException {
    return true;
  }

  /**
   * 是否为动态网页客户端
   *
   * @return true 动态网页 false 静态
   */
  default boolean isDynamic() {
    return false;
  }
}
