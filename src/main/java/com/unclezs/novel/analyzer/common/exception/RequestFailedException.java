package com.unclezs.novel.analyzer.common.exception;

import java.io.IOException;

/**
 * 请求超时异常
 *
 * @author blog.unclezs.com
 * @date 2021/5/4 12:08
 */
public class RequestFailedException extends IOException {
  public RequestFailedException() {
  }

  public RequestFailedException(String message) {
    super(message);
  }

  public RequestFailedException(String message, Throwable cause) {
    super(message, cause);
  }

  public RequestFailedException(Throwable cause) {
    super(cause);
  }
}
