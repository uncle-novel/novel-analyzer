package com.unclezs.novel.analyzer.spider.model;

import lombok.Data;

/**
 * 章节信息
 *
 * @author blog.unclezs.com
 * @date 2020/12/20 6:24 下午
 */
@Data
public class Chapter {
    /**
     * 章节名字
     */
    private String name;
    /**
     * 章节url
     */
    private String url;
}
