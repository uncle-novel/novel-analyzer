package com.unclezs.novel.analyzer.spider.pipline;

import com.unclezs.novel.analyzer.model.Chapter;

/**
 * 打印在控制台的pipeline
 *
 * @author blog.unclezs.com
 * @date 2020/12/23 15:56
 */
public class ConsolePipeline extends AbstractTextPipeline {
    @Override
    public void processChapter(Chapter chapter) {
        System.out.println(chapter);
    }
}
