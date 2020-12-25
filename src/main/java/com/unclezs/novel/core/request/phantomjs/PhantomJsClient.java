package com.unclezs.novel.core.request.phantomjs;

import com.unclezs.novel.core.request.RequestData;
import com.unclezs.novel.core.request.spi.HttpProvider;
import com.unclezs.novel.core.utils.CommandUtil;
import com.unclezs.novel.core.utils.FileUtil;
import com.unclezs.novel.core.utils.StringUtil;
import com.unclezs.novel.core.utils.SystemUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

/**
 * https://phantomjs.org/api/
 * <p>
 * 首先 添加system properties PHANTOMJS_PATH、PHANTOMJS_SCRIPT
 * <p>
 * 不然会使用默认位置去读取
 *
 * @author blog.unclezs.com
 * @since 2020/12/24 17:30
 */
@Slf4j
public class PhantomJsClient implements HttpProvider {
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

    /**
     * 获取网页内容
     *
     * @param data 请求数据
     * @return /
     * @throws IOException /
     */
    @Override
    public String content(RequestData data) throws IOException {
        return executePhantomJs(PhantomJsRequestData.from(data));
    }

    @Override
    public InputStream stream(RequestData requestData) throws IOException {
        throw new UnsupportedEncodingException("PhantomJs动态网页HTTP客户端不支持获取流");
    }

    @Override
    public boolean isDynamic() {
        return true;
    }

    public String content(String url) throws IOException {
        return content(RequestData.defaultRequestData(url));
    }

    /**
     * 执行PhantomJs脚本抓取动态网页
     * phantomjs [options] script.js [arg1 [arg2 [...]]]
     * @param data 请求数据
     * @return /
     */
    public String executePhantomJs(PhantomJsRequestData data) throws IOException {
        if (StringUtil.isEmpty(data.getUrl())) {
            log.warn("phantomJS request url 不能为空");
            return StringUtil.EMPTY;
        }
        StringBuilder command = new StringBuilder();
        // phantomJs
        command.append(System.getProperty(PHANTOMJS_PATH, DEFAULT_PHANTOMJS_PATH));
        // 忽略SSL错误
        command.append(StringUtil.BLANK).append("--ignore-ssl-errors=").append(data.isIgnoreSslError());
        // 不加载图片
        command.append(StringUtil.BLANK).append("--load-images=").append(data.isLoadImg());
        // HTTP代理
        if (StringUtil.isNotEmpty(data.getProxy())) {
            command.append(StringUtil.BLANK).append("--proxy=").append(data.getProxy());
        }
        // script
        command.append(StringUtil.BLANK).append(System.getProperty(PHANTOMJS_SCRIPT, DEFAULT_PHANTOMJS_SCRIPT));
        // args
        command.append(StringUtil.BLANK).append(data.getUrl());
        command.append(StringUtil.BLANK).append(data.getReferer());
        command.append(StringUtil.BLANK).append(data.getCookie());
        command.append(StringUtil.BLANK).append(data.getUserAgent());
        return CommandUtil.execute(command.toString());
    }
}
