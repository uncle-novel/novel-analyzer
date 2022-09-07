package com.unclezs.novel.analyzer.spider.pipline;

import com.unclezs.novel.analyzer.common.exception.SpiderRuntimeException;
import com.unclezs.novel.analyzer.model.Chapter;
import com.unclezs.novel.analyzer.request.Http;
import com.unclezs.novel.analyzer.request.RequestParams;
import com.unclezs.novel.analyzer.util.FileUtils;
import com.unclezs.novel.analyzer.util.StringUtils;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

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
public class MediaFilePipeline extends BaseFilePipeline {
  public static final String MEDIA_TYPE_M4A = "m4a";
  private static final String DOWNLOAD_FILE_FORMAT = "%s/%s.%s";
  private static final String MEDIA_TYPE_MP3 = "mp3";
  public static final int KB_5 = 5 * 1024;

  @Override
  public void process(Chapter chapter) {
    String downloadFile = String.format(DOWNLOAD_FILE_FORMAT, getFilePath(), StringUtils.removeInvalidSymbol(chapter.getName()), getType(chapter.getContent()));
    try {
      RequestParams requestParams = RequestParams.create(chapter.getContent());
      requestParams.addHeader(RequestParams.REFERER, chapter.getUrl());
      byte[] bytes = Http.bytes(requestParams);
      if (bytes.length < KB_5) {
        throw new SpiderRuntimeException("音频大小太小，认定为失败");
      }
      FileUtils.writeBytes(downloadFile, bytes);
    } catch (IOException e) {
      log.error("保存章节内容到：{} 失败.", downloadFile, e);
      throw new SpiderRuntimeException("音频抓取失败");
    }
  }

  /**
   * 获取音频类型
   *
   * @param page URL
   * @return 类型
   */
  private String getType(String page) {
    int typeSplitIndex = page.lastIndexOf(".");
    if (typeSplitIndex > 0) {
      String type = page.substring(typeSplitIndex + 1);
      if (MEDIA_TYPE_M4A.equalsIgnoreCase(type)) {
        return MEDIA_TYPE_M4A;
      }
    }
    return MEDIA_TYPE_MP3;
  }
}
