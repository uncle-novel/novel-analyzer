package com.unclezs.novel.analyzer.util;

import org.junit.Test;

import java.io.IOException;

/**
 * @author blog.unclezs.com
 * @since 2021/01/06 16:00
 */
public class FileUtilsTest {
    @Test
    public void testTouch() throws IOException {
//        FileUtils.mkdirs(FileUtils.USER_DIR+"/downloads");
        FileUtils.touch(FileUtils.USER_DIR+"/downloads/xx.txt");
    }
}
