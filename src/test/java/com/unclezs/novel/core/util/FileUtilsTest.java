package com.unclezs.novel.core.util;

import org.junit.Test;

import java.io.IOException;

/**
 * @author zhanghongguo@sensorsdata.cn
 * @since 2021/01/06 16:00
 */
public class FileUtilsTest {
    @Test
    public void testTouch() throws IOException {
//        FileUtils.mkdirs(FileUtils.USER_DIR+"/downloads");
        FileUtils.touch(FileUtils.USER_DIR+"/downloads/xx.txt");
    }
}
