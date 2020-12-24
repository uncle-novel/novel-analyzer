package com.unclezs.novel.core.model;

import lombok.Data;

import java.util.List;

/**
 * @author blog.unclezs.com
 * @since 2020/12/23 18:42
 */
@Data
public class TextNovel implements Novel {
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
    /**
     * 章节列表
     */
    private List<Chapter> chapters;
}
