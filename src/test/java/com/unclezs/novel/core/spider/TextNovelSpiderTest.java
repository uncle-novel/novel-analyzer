package com.unclezs.novel.core.spider;

import com.unclezs.novel.core.request.RequestData;
import com.unclezs.novel.core.util.Console;
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
        RequestData requestData = RequestData.defaultRequestData("http://www.dmbj.cc/daomubiji1/");
        RequestData contentRequestData = RequestData.defaultRequestData("http://www.dmbj.cc/daomubiji1/1.html");
        novelSpider.crawling(requestData, chapter -> {
            Console.println("{}.《{}》 章节URL：{}", chapter.getOrder(), chapter.getName(), chapter.getUrl());
            String content = null;
            try {
                content = novelSpider.content(RequestData.defaultRequestData(chapter.getUrl()));
                chapter.setContent(content);
                Console.println(content);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
//        System.out.println(novelSpider.content(contentRequestData));
    }
}
