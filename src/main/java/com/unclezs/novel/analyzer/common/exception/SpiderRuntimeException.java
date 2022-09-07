package com.unclezs.novel.analyzer.common.exception;

/**
 * 爬虫运行时异常
 *
 * @author blog.unclezs.com
 * @since 2021/2/14 21:37
 */
public class SpiderRuntimeException extends RuntimeException {
  public SpiderRuntimeException() {
  }

  public SpiderRuntimeException(String message) {
    super(message);
  }
}
