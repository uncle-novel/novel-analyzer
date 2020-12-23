package com.unclezs.novel.core.spider.pipline;

import com.unclezs.novel.core.model.Chapter;

/**
 * @author blog.unclezs.com
 * @date 2020/12/23 10:58 下午
 */
public class FilePipeline implements Pipeline<Chapter> {
    @Override
    public void process(Chapter data) {
        System.out.println(data.getContent());
    }
}
