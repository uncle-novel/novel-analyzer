package com.unclezs.novel.analyzer.spider.pipline;

import com.unclezs.novel.analyzer.model.Chapter;
import com.unclezs.novel.analyzer.spider.helper.SpiderHelper;
import com.unclezs.novel.analyzer.util.FileUtils;
import com.unclezs.novel.analyzer.util.StringUtils;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;

/**
 * 保存为文本文件的pipeline
 * 路径为当前目录的下的downloads
 *
 * @author blog.unclezs.com
 * @since 2020/12/23 10:58 下午
 */
@Slf4j
@Setter
public class TxtPipeline extends AbstractTextPipeline {
  private static final String DOWNLOAD_FILE_FORMAT = "%s/%d.%s.txt";
  /**
   * 是否合并文件
   */
  private boolean merge;
  /**
   * 删除章节文件
   */
  private boolean deleteVolume;

  @Override
  public void processChapter(Chapter chapter) {
    String filePath = String.format(DOWNLOAD_FILE_FORMAT, getFilePath(), chapter.getOrder(), StringUtils.removeInvalidSymbol(chapter.getName()));
    try {
      // 写入文件
      FileUtils.writeString(filePath, chapter.getContent(), getCharset());
    } catch (IOException e) {
      log.error("保存章节内容到：{} 失败.", filePath, e);
    }
  }

  @Override
  public void onComplete() {
    if (merge) {
      try {
        SpiderHelper.mergeNovel(new File(getFilePath()), getNovel(), deleteVolume);
      } catch (Exception e) {
        log.error("文件合并失败：{}", getFilePath(), e);
      }
    }
  }
}
