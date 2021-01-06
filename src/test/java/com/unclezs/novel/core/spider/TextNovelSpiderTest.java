package com.unclezs.novel.core.spider;

import com.unclezs.novel.core.AnalyzerManager;
import com.unclezs.novel.core.request.RequestData;
import com.unclezs.novel.core.spider.pipline.StoreTextFilePipeline;
import com.unclezs.novel.core.util.RandomUtils;
import org.junit.Test;

import java.io.IOException;

/**
 * @author blog.unclezs.com
 * @date 2021/1/6 7:37
 */
public class TextNovelSpiderTest {
    @Test
    public void testConcurrentSpider() throws IOException {
        TextNovelSpider novelSpider = new TextNovelSpider();
        AnalyzerManager.autoProxy(true);
        RequestData requestData = RequestData.defaultBuilder("http://www.dmbj.cc/daomubiji1/").enableProxy(AnalyzerManager.enableAutoProxy()).build();
        novelSpider.crawling(requestData, new StoreTextFilePipeline(RandomUtils.randomInt(1000) + ""), 3);
    }
}
