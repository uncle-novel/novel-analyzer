package com.unclezs.novel.analyzer.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author blog.unclezs.com
 * @date 2020/12/21 14:41
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pair<K, V> {
    private K left;
    private V right;

    public static <K, V> Pair<K, V> of(K left, V right) {
        return new Pair<>(left, right);
    }
}
