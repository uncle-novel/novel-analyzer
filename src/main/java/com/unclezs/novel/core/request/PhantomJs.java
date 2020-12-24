package com.unclezs.novel.core.request;

import com.unclezs.novel.core.utils.CollectionUtil;
import com.unclezs.novel.core.utils.FileUtil;
import com.unclezs.novel.core.utils.StringUtil;
import com.unclezs.novel.core.utils.SystemUtil;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * 首先 添加system properties PHANTOMJS_PATH、PHANTOMJS_SCRIPT
 * <p>
 * 不然会使用默认位置去读取
 *
 * @author blog.unclezs.com
 * @since 2020/12/24 17:30
 */
@Slf4j
@UtilityClass
public class PhantomJs {
    /**
     * PhantomJs执行文件的位置
     */
    public static final String PHANTOMJS_PATH = "PHANTOMJS_PATH";
    /**
     * 脚本位置
     */
    public static final String PHANTOMJS_SCRIPT = "PHANTOMJS_SCRIPT_PATH";
    public static final String DEFAULT_PHANTOMJS_SCRIPT = FileUtil.USER_DIR + "/script/spider.js";
    public static final String DEFAULT_PHANTOMJS_PATH =
        FileUtil.USER_DIR + "/script/phantomjs" + SystemUtil.getExecuteSuffix();
    public static final String FAILED_TAG = "failed";

    public static String content(RequestData data) {
        String userAgent = StringUtil.EMPTY;
        String cookie = StringUtil.EMPTY;
        String referer = StringUtil.EMPTY;
        if (CollectionUtil.isNotEmpty(data.getHeaders())) {
            userAgent = data.getHeaders().getOrDefault(RequestData.USER_AGENT, StringUtil.EMPTY);
            cookie = data.getHeaders().getOrDefault(RequestData.COOKIE, StringUtil.EMPTY);
            referer = data.getHeaders().getOrDefault(RequestData.REFERER, StringUtil.EMPTY);
        }
        return executePhantomJs(data.getUrl(), referer, cookie, userAgent);
    }

    public static String content(String url) {
        return content(RequestData.defaultRequestData(url));
    }

    /**
     * 执行PhantomJs脚本抓取动态网页
     *
     * @param url       /
     * @param referer   /
     * @param cookie    /
     * @param userAgent /
     * @return /
     */
    public static String executePhantomJs(String url, String referer, String cookie, String userAgent) {
        StringBuilder command = new StringBuilder();
        command.append(System.getProperty(PHANTOMJS_PATH, DEFAULT_PHANTOMJS_PATH)).append(StringUtil.EMPTY).append(
            System.getProperty(PHANTOMJS_SCRIPT, DEFAULT_PHANTOMJS_SCRIPT));
        command.append(StringUtil.EMPTY).append(url);
        command.append(StringUtil.EMPTY).append(referer);
        command.append(StringUtil.EMPTY).append(cookie);
        command.append(StringUtil.EMPTY).append(userAgent);
        Process process;
        StringBuilder buffer = new StringBuilder();
        log.trace("PhantomJs抓取网页：执行命令：{}", command);
        try {
            process = Runtime.getRuntime().exec(command.toString());
            InputStream is = process.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String tmp;
            while ((tmp = br.readLine()) != null) {
                buffer.append(tmp).append(StringUtil.NEW_LINE);
            }
        } catch (IOException e) {
            log.warn("PhantomJs抓取网页失败：执行命令：{}", command.toString());
            return StringUtil.EMPTY;
        }
        String html = buffer.toString();
        // 处理失败了的情况
        if (html.length() < 10 && html.contains(FAILED_TAG)) {
            return StringUtil.EMPTY;
        }
        return html;
    }
}
