package com.unclezs.novel.analyzer.spider.helper;

import com.unclezs.novel.analyzer.core.matcher.matchers.XpathMatcher;
import com.unclezs.novel.analyzer.core.model.Params;
import com.unclezs.novel.analyzer.request.Http;
import com.unclezs.novel.analyzer.request.RequestParams;
import com.unclezs.novel.analyzer.script.ScriptContext;
import com.unclezs.novel.analyzer.util.FileUtils;
import com.unclezs.novel.analyzer.util.StringUtils;
import com.unclezs.novel.analyzer.util.uri.UrlUtils;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 * @author blog.unclezs.com
 * @date 2021/02/10 16:53
 */
@Slf4j
@UtilityClass
public class SpiderHelper {

  public static final String COVER_PROVIDER_URL = "https://www.qidian.com/search?kw=";

  /**
   * 请求参数预处理
   * 1. 配置自定义的请求的参数
   * 2. 脚本变量初始化
   *
   * @param defaultParams 默认的规则
   * @param params        要处理的请求参数
   */
  public static String request(Params defaultParams, RequestParams params) throws IOException {
    params.overrideParams(defaultParams);
    String content;
    try {
      // 脚本初始变量添加 当前页面URL，当前的请求参数
      ScriptContext.put(ScriptContext.SCRIPT_CONTEXT_VAR_URL, params.getUrl());
      ScriptContext.put(ScriptContext.SCRIPT_CONTEXT_VAR_PARAMS, params);
      content = Http.content(params);
    } catch (Exception e) {
      // 请求失败则移除脚本上下文
      ScriptContext.remove();
      throw new IOException(e);
    }
    return content;
  }

  /**
   * 请求参数预处理
   * 1. 配置自定义的请求的参数
   * 2. 脚本变量初始化
   *
   * @param params 要处理的请求参数
   */
  public static String request(RequestParams params) throws IOException {
    return request(null, params);
  }

  /**
   * 合并文件
   *
   * @param dir      小说目录
   * @param filename 文件
   * @param delete   合并后删除
   */
  public static void mergeNovel(File dir, String filename, boolean delete) throws IOException {
    // 保存到父目录下
    String saveFile = new File(dir.getParent(), filename).getAbsolutePath();
    FileUtils.deleteFile(saveFile);
    File[] txtFiles = dir.listFiles((dir1, name) -> name.endsWith(".txt"));
    if (txtFiles != null) {
      Arrays.stream(txtFiles).sorted((o1, o2) -> {
        Integer a = Integer.valueOf(o1.getName().split("\\.")[0]);
        Integer b = Integer.valueOf(o2.getName().split("\\.")[0]);
        return a - b;
      }).forEach(file -> {
        try {
          String s = FileUtils.readUtf8String(file.getAbsoluteFile());
          FileUtils.appendUtf8String(saveFile, s);
          if (delete) {
            FileUtils.deleteFile(file);
          }
        } catch (IOException e) {
          log.error("小说合并失败：文件夹：{}，文件名：{}", dir, filename, e);
          e.printStackTrace();
        }
      });
    }
    if (delete) {
      FileUtils.deleteFile(dir);
    }
  }

  /**
   * 移除文本中的标题
   *
   * @param src    源文本
   * @param target 要移除的文本
   * @return /
   */
  public static String removeTitle(String src, String target) {
    String[] lines = src.split(StringUtils.LF);
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < lines.length; i++) {
      if (StringUtils.isNotBlank(lines[i])) {
        if (i < 3) {
          lines[i] = lines[i].replace(target, StringUtils.EMPTY)
            .replace(target.replace(StringUtils.BLANK, StringUtils.EMPTY), StringUtils.EMPTY);
        }
        sb.append(lines[i]).append(StringUtils.NEW_LINE);
      }
    }
    return sb.toString();
  }

  /**
   * 获取封面
   *
   * @param title 小说名称
   * @return cover url
   */
  public static String getCover(String title) throws IOException {
    RequestParams params = RequestParams.create(COVER_PROVIDER_URL + UrlUtils.encode(title));
    String html = Http.content(params);
    String cover = XpathMatcher.me().match(html, "//*[@id=\"result-list\"]/div/ul/li[1]/div[1]/a/img/@src");
    cover = UrlUtils.completeUrl(COVER_PROVIDER_URL, cover);
    return cover;
  }
}
