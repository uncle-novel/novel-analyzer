package com.unclezs.novel.analyzer.request.phantomjs;

import com.unclezs.novel.analyzer.request.RequestParams;
import com.unclezs.novel.analyzer.request.spi.HttpProvider;
import com.unclezs.novel.analyzer.util.CommandUtils;
import com.unclezs.novel.analyzer.util.FileUtils;
import com.unclezs.novel.analyzer.util.StringUtils;
import com.unclezs.novel.analyzer.util.SystemUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * https://phantomjs.org/api/
 * <p>
 * 首先 添加system properties PHANTOMJS_PATH、PHANTOMJS_SCRIPT
 * <p>
 * 不然会使用默认位置去读取
 *
 * @author blog.unclezs.com
 * @date 2020/12/24 17:30
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
    public static final String DEFAULT_PHANTOMJS_SCRIPT = FileUtils.USER_DIR + "/script/spider.js";
    public static final String DEFAULT_PHANTOMJS_PATH = FileUtils.USER_DIR + "/script/phantomjs" + SystemUtils.getExecuteSuffix();

    /**
     * 获取网页内容
     *
     * @param data 请求数据
     * @return /
     * @throws IOException /
     */
    @Override
    public String content(RequestParams data) throws IOException {
        // 请求数据转化
        return executePhantomJs(PhantomJsRequestParams.from(data));
    }

    @Override
    public byte[] bytes(RequestParams requestParams) throws IOException {
        throw new UnsupportedEncodingException("PhantomJs动态网页HTTP客户端不支持获取流");
    }

    @Override
    public boolean isDynamic() {
        return true;
    }

    public String content(String url) throws IOException {
        return content(RequestParams.create(url));
    }

    /**
     * 执行PhantomJs脚本抓取动态网页
     * phantomjs [options] script.js [arg1 [arg2 [...]]]
     *
     * @param data 请求数据
     * @return /
     */
    public String executePhantomJs(PhantomJsRequestParams data) throws IOException {
        if (StringUtils.isEmpty(data.getUrl())) {
            log.warn("phantomJS com.unclezs.novel.analyzer.request url 不能为空");
            return StringUtils.EMPTY;
        }
        StringBuilder command = new StringBuilder();
        // phantomJs
        command.append(System.getProperty(PHANTOMJS_PATH, DEFAULT_PHANTOMJS_PATH));
        // 忽略SSL错误
        command.append(StringUtils.BLANK).append("--ignore-ssl-errors=").append(data.isIgnoreSslError());
        // 不加载图片
        command.append(StringUtils.BLANK).append("--load-images=").append(data.isLoadImg());
        // HTTP代理
        if (StringUtils.isNotEmpty(data.getProxy())) {
            command.append(StringUtils.BLANK).append("--proxy=").append(data.getProxy());
        }
        // script
        command.append(StringUtils.BLANK).append(System.getProperty(PHANTOMJS_SCRIPT, DEFAULT_PHANTOMJS_SCRIPT));
        // args
        command.append(StringUtils.BLANK).append("\"").append(data.getUrl()).append("\"");
        command.append(StringUtils.BLANK).append("\"").append(data.getReferer()).append("\"");
        command.append(StringUtils.BLANK).append("\"").append(data.getCookie()).append("\"");
        command.append(StringUtils.BLANK).append("\"").append(data.getUserAgent()).append("\"");
        String execute = CommandUtils.execute(command.toString());
        System.out.println(execute);
        return execute;
    }
}
