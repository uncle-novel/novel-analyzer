package com.unclezs.novel.analyzer.utils;

import com.unclezs.novel.analyzer.utils.regex.ReUtil;

/**
 * URL工具类
 *
 * @author uncle
 * @date 2020/3/25 21:11
 */
public class UrlUtil {
    /**
     * 获取URL的域名
     *
     * @param url /
     * @return 域名
     */
    public static String getHost(String url) {
        return ReUtil.get("http[s]{0,1}://(.+?)/", url, 1);
    }

    /**
     * 获取host
     *
     * @param url /   https://www.unclezs.com  => unclezs
     * @return /
     */
    public static String getSite(String url) {
        String host = ReUtil.get("http[s]{0,1}://(.+?)/", url+"/", 1);
        String[] str = host.split("\\.");
        if (str.length == 3) {
            return str[1];
        } else {
            return str[0];
        }
    }
    /**
     * 取出URL中最后一段
     * https://unclezs.com/abc.html  则得到 abc
     *
     * @param url /
     * @return /
     */
    public static String getUrlLastPathNotSuffix(String url) {
        String str = url.replaceAll("\\.htm.*", "");
        int i = str.lastIndexOf("/");
        return str.substring(i + 1);
    }


    /**
     * 是否为http链接
     *
     * @param url /
     * @return /
     */
    public static boolean isHttpUrl(String url) {
        return StringUtil.isNotEmpty(url) && url.toLowerCase().startsWith("http");
    }


    /**
     * url是否不包含锚点
     *
     * @param url /
     * @return /
     */
    public static boolean notAnchor(String url) {
        return isHttpUrl(url) && !url.contains("#");
    }
}
