package com.unclezs.novel.analyzer.common.exception;

/**
 * 工具类异常
 *
 * @author blog.unclezs.com
 * @date 2020/12/22 11:12
 */
public class UtilException extends RuntimeException {

    public UtilException(String message) {
        super(message);
    }

    public UtilException(Throwable cause) {
        super(cause);
    }
}
