package com.unclezs.novel.analyzer;

import com.unclezs.novel.analyzer.common.concurrent.ThreadUtils;
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
        ThreadPoolExecutor service = ThreadUtils.newFixedThreadPoolExecutor(2, "ass");
        System.out.println("add");
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
        service.purge();
        service.execute(new Runnable() {
            @Override
            public void run() {
                System.out.println("我是最后添加的");
            }
        });
        System.out.println("add1");
        ThreadUtils.sleep(10000L);
        service.shutdown();
        System.out.println(666);
    }
}
