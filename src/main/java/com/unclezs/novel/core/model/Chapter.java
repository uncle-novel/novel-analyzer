package com.unclezs.novel.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

/**
 * 章节信息
 *
 * @author blog.unclezs.com
 * @date 2020/12/20 6:24 下午
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Chapter {
    /**
     * 章节序号
     */
    private int order = -1;
    /**
     * 章节名字
     */
    private String name;
    /**
     * 章节url
     */
    private String url;
    /**
     * 正文
     */
    private String content;

    public Chapter(String name, String url) {
        this.name = name;
        this.url = url;
    }

    /**
     * 只根据URL分别唯一
     *
     * @param o other
     * @return /
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Chapter chapter = (Chapter) o;
        return Objects.equals(url, chapter.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url);
    }

    @Override
    public String toString() {
        return String.format("name=%s,url=%s", name, url);
    }
}
