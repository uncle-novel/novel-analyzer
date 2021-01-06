package com.unclezs.novel.core;

import com.unclezs.novel.core.concurrent.pool.ThreadPoolUtil;
import org.junit.Test;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author blog.unclezs.com
 * @since 2020/12/22 15:05
 */
public class AppTest {
    public int cnt = 1;

    @Test
    public void test() {
        ThreadPoolExecutor service = ThreadPoolUtil.newFixedThreadPoolExecutor(2, "ass");
        for (int i = 0; i < 10; i++) {
            service.submit(() -> {
                System.out.println(cnt++);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
        service.shutdown();
        System.out.println(666);
    }
}
