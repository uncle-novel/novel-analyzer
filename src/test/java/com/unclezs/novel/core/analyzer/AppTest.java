package com.unclezs.novel.core.analyzer;

import com.unclezs.novel.core.concurrent.pool.ThreadPool;
import com.unclezs.novel.core.concurrent.pool.ThreadPoolUtil;
import lombok.SneakyThrows;
import org.junit.Test;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author blog.unclezs.com
 * @since 2020/12/22 15:05
 */
public class AppTest {
    public int cnt = 1;

    @Test
    public void test() throws InterruptedException {
        ThreadPool service = ThreadPoolUtil.newFixedThreadPoolExecutor(2, "ass");
        for (int i = 0; i < 10; i++) {
            service.submit(new Runnable() {
                @SneakyThrows
                @Override
                public void run() {
                    System.out.println(cnt++);
                    Thread.sleep(1000);
                }
            });
        }
        service.shutdown();
        System.out.println(666);
        boolean b = service.awaitTermination(Integer.MAX_VALUE, TimeUnit.DAYS);

    }
}
