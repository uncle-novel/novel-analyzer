package com.unclezs.novel.analyzer.spider;

import com.unclezs.novel.analyzer.core.model.TextAnalyzerConfig;
import com.unclezs.novel.analyzer.core.text.TextNovelAnalyzer;
import com.unclezs.novel.analyzer.request.Http;
import com.unclezs.novel.analyzer.request.RequestData;
import com.unclezs.novel.analyzer.utils.uri.UrlUtil;
import lombok.Data;

import java.io.IOException;

/**
 * @author blog.unclezs.com
 * @date 2020/12/20 6:17 下午
 */
@Data
public class TextNovelSpider {
    /**
     * 配置信息
     */
    private TextAnalyzerConfig config;


    public TextNovelSpider() {
        this(TextAnalyzerConfig.defaultConfig());
    }

    public TextNovelSpider(TextAnalyzerConfig config) {
        this.config = config;
    }


    public String content(RequestData requestData) throws IOException {
        String originalText;
        if (config.getNextPageRule().isEmpty()) {
            originalText = TextNovelAnalyzer.content(Http.content(requestData), config);
        } else {
            StringBuilder pages = new StringBuilder();
            String rule = config.getNextPageRule();
            String nextPage = requestData.getUrl();
            while (UrlUtil.isHttpUrl(nextPage)) {
                requestData.setUrl(nextPage);
                String page = Http.content(requestData);
                pages.append(TextNovelAnalyzer.content(page, config));
                nextPage = TextNovelAnalyzer.nextPage(page, config);
            }
            originalText = pages.toString();
        }
        return originalText;
    }

}
