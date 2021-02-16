package com.unclezs.novel.analyzer.common.exception;

/**
 * 匹配器运行时异常
 *
 * @author blog.unclezs.com
 * @date 2021/02/10 16:23
 */
public class MatcherRuntimeException extends RuntimeException {
    public MatcherRuntimeException(String message) {
        super(message);
    }

    public MatcherRuntimeException() {
    }
}
