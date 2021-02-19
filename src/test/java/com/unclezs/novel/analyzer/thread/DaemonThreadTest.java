package com.unclezs.novel.analyzer.thread;

import com.unclezs.novel.analyzer.common.concurrent.ThreadUtils;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author blog.unclezs.com
 * @since 2021/1/17 22:34
 */
public class DaemonThreadTest {
    public static void main(String[] args) throws InterruptedException {
        ThreadPoolExecutor ss = ThreadUtils.newSingleThreadExecutor("ss");
        ss.execute(() -> {
            while (true) {
                System.out.println("123");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        Thread.sleep(1000);
    }
}
