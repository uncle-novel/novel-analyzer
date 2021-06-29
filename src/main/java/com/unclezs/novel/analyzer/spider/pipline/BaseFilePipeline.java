package com.unclezs.novel.analyzer.spider.pipline;

import com.unclezs.novel.analyzer.util.FileUtils;
import com.unclezs.novel.analyzer.util.StringUtils;
import lombok.Setter;

/**
 * 管道处理基类
 *
 * @author blog.unclezs.com
 * @date 2021/02/08 11:02
 */
@Setter
public abstract class BaseFilePipeline extends BasePipeline {
  /**
   * 默认保存路径 非final，可以自己定义
   */
  protected static String defaultPath = FileUtils.USER_DIR + "/downloads/";
  /**
   * 默认文件名称
   */
  protected static String defaultFileName = "公众号书虫无书荒";
  private String path;

  /**
   * 获取文件保存路径
   *
   * @return 文件路径
   */
  public String getPath() {
    return path == null ? defaultPath : path;
  }

  /**
   * 设置文件保存路径
   *
   * @param path 路径
   */
  public void setPath(String path) {
    path = path.replace(StringUtils.SLASH, StringUtils.BACKSLASH).trim();
    if (!path.endsWith(StringUtils.BACKSLASH)) {
      path = path.concat(StringUtils.BACKSLASH);
    }
    this.path = path.trim();
  }

  /**
   * 获取带小说名称的保存路径
   *
   * @return 带小说名称的保存路径
   */
  public String getFilePath() {
    if (getNovel() != null && StringUtils.isNotBlank(getNovel().getTitle())) {
      return getPath() + StringUtils.removeInvalidSymbol(getNovel().getTitle()).trim();
    }
    return getPath() + defaultFileName.trim();
  }
}
