package com.unclezs.novel.analyzer.spider.helper;

import com.unclezs.novel.analyzer.request.HttpMethod;
import com.unclezs.novel.analyzer.request.RequestParams;
import com.unclezs.novel.analyzer.script.ScriptUtils;
import com.unclezs.novel.analyzer.util.StringUtils;
import com.unclezs.novel.analyzer.util.regex.RegexUtils;
import com.unclezs.novel.analyzer.util.uri.UrlEncoder;
import com.unclezs.novel.analyzer.util.uri.UrlUtils;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import javax.script.Bindings;
import javax.script.SimpleBindings;
import java.util.HashMap;
import java.util.Map;

/**
 * 搜索辅助类
 *
 * @author blog.unclezs.com
 * @date 2021/02/09 15:50
 */
@Slf4j
@UtilityClass
public class SearchHelper {
  /**
   * 搜索参数 关键词
   */
  public static final String SEARCH_KEYWORD = "keyword";
  /**
   * 搜索参数 页码
   */
  public static final String SEARCH_PAGE = "page";

  /**
   * 获取计算后的参数
   * page={{page}}&amp;key={{keyword}} 编译为 page=1&amp;key={{keyword}}
   *
   * @param src        源字符串  page={{page}}&amp;key={{keyword}}
   * @param param      参数
   * @param paramValue 参数值
   * @return 参数处理后结果
   */
  public static String pretreatmentParam(String src, String param, String paramValue) {
    String script = RegexUtils.get("\\{\\{([^{]*?" + param + ".*?)\\}\\}", src, 1);
    // 是脚本则执行脚本获取参数
    if (StringUtils.isNotBlank(script) && !script.trim().equalsIgnoreCase(param)) {
      Bindings bindings = new SimpleBindings();
      bindings.put(param, paramValue);
      paramValue = ScriptUtils.execute(script, bindings);
    }
    return src.replaceAll("\\{\\{[^{]*?" + param + ".*?\\}\\}", paramValue);
  }

  /**
   * 获取计算后的参数
   * <p>
   * 拓展{@link SearchHelper#pretreatmentParam}
   *
   * @param src    源字符串  page={{page}}&amp;key={{keyword}}
   * @param params 参数 key参数名 value参数值
   * @return 参数处理后结果
   */
  public static String pretreatmentParams(String src, Map<String, String> params) {
    for (Map.Entry<String, String> param : params.entrySet()) {
      src = pretreatmentParam(src, param.getKey(), param.getValue());
    }
    return src;
  }

  /**
   * 预处理搜索参数
   * <p>
   * page={{page}}&amp;key={{keyword}}
   * <p>
   * 也支持URL参数格式 https://book.com/{{page}}/?keyword={{keyword}}
   *
   * @param params  请求参数
   * @param page    页码
   * @param keyword 关键词
   */
  public static void pretreatmentSearchParam(RequestParams params, int page, String keyword) {
    // 获取搜索参数 url、body   eg: page={{page}}&key={{keyword}}
    String searchUrlParams = UrlUtils.getUrlParams(params.getUrl());
    if (searchUrlParams.isEmpty()) {
      searchUrlParams = params.getBody();
    }
    final String searchParams = searchUrlParams;
    // 参数编码
    String encodeKeyword;
    if (StringUtils.isNotBlank(params.getCharset())) {
      encodeKeyword = UrlEncoder.encode(keyword, params.getCharset());
    } else {
      encodeKeyword = keyword;
    }
    // 搜索的URL 不带参数
    String url = UrlUtils.getUrlNoParams(params.getUrl());
    // 清空参数 重新编译参数
    params.setUrl(url);
    params.setBody(StringUtils.EMPTY);
    // 搜索参数封装
    Map<String, String> paramsMap = new HashMap<>(2);
    paramsMap.put(SEARCH_PAGE, String.valueOf(page));
    paramsMap.put(SEARCH_KEYWORD, encodeKeyword);
    // 编译参数
    String formattedSearchParam = pretreatmentParams(searchParams, paramsMap);
    url = pretreatmentParams(url, paramsMap);
    if (HttpMethod.GET.name().equalsIgnoreCase(params.getMethod())) {
      params.setUrl(String.format("%s?%s", url, formattedSearchParam));
    } else {
      params.setBody(formattedSearchParam);
    }
  }
}
