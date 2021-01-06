package com.unclezs.novel.core.util;

import lombok.experimental.UtilityClass;
import org.slf4j.helpers.MessageFormatter;

/**
 * 控制台输出
 * <p>
 * eg. Console.println("you are {}","beautiful")
 *
 * @author blog.unclezs.com
 * @date 2021/1/6 8:11
 */
@UtilityClass
public class Console {
    /**
     * 输出并换行
     *
     * @param msg  消息
     * @param args 参数
     */
    public void println(String msg, Object... args) {
        System.out.println(MessageFormatter.arrayFormat(msg, args).getMessage());
    }

    /**
     * 输出不换行
     *
     * @param msg  消息
     * @param args 参数
     */
    public void print(String msg, Object... args) {
        System.out.print(MessageFormatter.arrayFormat(msg, args).getMessage());
    }
}
