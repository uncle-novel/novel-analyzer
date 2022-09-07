package com.unclezs.novel.analyzer.spider;

import lombok.Data;

/**
 * @author blog.unclezs.com
 * @since 2021/4/23 2:09
 */
@Data
public class Result<T> {
  private int page;
  private T data;

  public Result(int page, T data) {
    this.page = page;
    this.data = data;
  }
}
