package com.unclezs.novel.analyzer.common.exception;

/**
 * 规则解析错误
 *
 * @author blog.unclezs.com
 * @since 2021/01/28 18:14
 */
public class RuleGrammarException extends RuntimeException {
    public RuleGrammarException(String message) {
        super(message);
    }

    public RuleGrammarException(Throwable cause) {
        super(cause);
    }
}
