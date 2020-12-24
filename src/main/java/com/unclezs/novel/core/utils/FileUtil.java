package com.unclezs.novel.core.utils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.io.File;

/**
 * 文件工具
 *
 * @author blog.unclezs.com
 * @date 2020/12/20 3:02 下午
 */
@Slf4j
@UtilityClass
public class FileUtil {
    public final String USER_DIR = System.getProperty("user.dir");

    static {
        log.debug("当前工作目录：{}", USER_DIR);
    }

    /**
     * 文件是否存在
     *
     * @param path /
     * @return /
     */
    public boolean exist(String path) {
        return new File(path).exists();
    }
}
