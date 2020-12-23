package com.unclezs.novel.core.spider.pipline;

/**
 * 打印在控制台的pipeline
 *
 * @author zhanghongguo@sensorsdata.cn
 * @since 2020/12/23 15:56
 */
public class ConsolePipeline<T> implements Pipeline<T> {
    @Override
    public void process(T data) {
        System.out.println(data);
    }
}
