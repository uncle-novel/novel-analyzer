package com.unclezs.novel.analyzer.model;

/**
 * 章节状态
 *
 * @author blog.unclezs.com
 * @since 2021/01/12 12:12
 */
public enum ChapterState {
    /**
     * 已经下载
     */
    DOWNLOADED,
    /**
     * 下载失败
     */
    FAILED,
    /**
     * 初始化完成还未下载
     */
    INIT
}
