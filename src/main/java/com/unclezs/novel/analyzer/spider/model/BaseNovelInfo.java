package com.unclezs.novel.analyzer.spider.model;

import lombok.Data;

/**
 * @author blog.unclezs.com
 * @date 2020/12/20 6:18 下午
 */
@Data
public class BaseNovelInfo {
    /**
     * 目录链接
     */
    protected String url;
    /**
     * 作者
     */
    protected String author;
    /**
     * 名字
     */
    protected String title;
    /**
     * 封面
     */
    protected String cover;
    /**
     * 简介
     */
    protected String desc;
}
