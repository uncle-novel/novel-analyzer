package com.unclezs.novel.analyzer.spider.pipline;

import com.unclezs.novel.analyzer.model.Novel;
import lombok.Getter;
import lombok.Setter;

/**
 * 管道处理基类
 *
 * @author blog.unclezs.com
 * @since 2021/02/08 11:02
 */
@Getter
@Setter
public abstract class BasePipeline implements Pipeline {
  private Novel novel = null;

  @Override
  public void injectNovel(Novel novel) {
    this.novel = novel;
  }
}
