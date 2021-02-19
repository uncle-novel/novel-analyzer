package com.unclezs.novel.analyzer.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 别名
 * 用于一些映射，比如规则到model
 *
 * @author blog.unclezs.com
 * @date 2021/02/09 11:19
 * @see com.unclezs.novel.analyzer.core.matcher.Matchers#getChildMap
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Alias {
    String value();
}
