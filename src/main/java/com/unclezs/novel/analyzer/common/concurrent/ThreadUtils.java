package com.unclezs.novel.analyzer.common.concurrent;

import com.unclezs.novel.analyzer.common.concurrent.factory.DaemonThreadFactory;
import com.unclezs.novel.analyzer.common.concurrent.reject.BlockRejectHandler;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程工具类
 * 注意：创建的线程都是守护线程
 * <p>
 * <li>1.创建线程池</li>
 * <li>2.创建线程</li>
 *
 * @author blog.unclezs.com
 * @since 2020/12/20 4:50 下午
 */
@Slf4j
@UtilityClass
public class ThreadUtils {
  private static final ThreadPoolExecutor GLOBAL_THREAD_POOL = ThreadUtils.newCachedThreadPool("global");

  /**
   * 提交全局线程池 执行任务
   *
   * @param task /
   */
  public static void execute(Runnable task) {
    GLOBAL_THREAD_POOL.execute(task);
  }

  /**
   * 创建一个线程
   *
   * @param runnable 执行的task
   * @param daemon   是否为守护线程
   * @return /
   */
  public static Thread newThread(Runnable runnable, boolean daemon) {
    Thread thread = GLOBAL_THREAD_POOL.getThreadFactory().newThread(runnable);
    thread.setDaemon(daemon);
    return thread;
  }

  /**
   * 阻塞当前线程
   *
   * @param time 阻塞时间
   */
  public static void sleep(long time) {
    try {
      Thread.currentThread().join(time);
    } catch (InterruptedException e) {
      log.error("sleep error", e);
      Thread.currentThread().interrupt();
    }
  }

  /**
   * 提交全局线程池 执行可检测返回任务
   *
   * @param task /
   */
  public static <T> Future<T> execute(Callable<T> task) {
    return GLOBAL_THREAD_POOL.submit(task);
  }

  /**
   * 创建一个单线程池，同newFixedThreadPoolExecutor
   * <p>
   * 无界队列，饱和策略失效
   * 核心线程永不过期
   *
   * @param name 线程池名称
   * @return /
   */
  public static ThreadPoolExecutor newSingleThreadExecutor(String name) {
    return new ThreadPoolExecutor(1, 1,
      0L, TimeUnit.MILLISECONDS,
      new LinkedBlockingQueue<>(), new DaemonThreadFactory(name));
  }

  /**
   * 创建一个队列长度无限的，任务可以随便添加不会阻塞，线程数固定
   * <p>
   * 无界队列，饱和策略失效
   * 核心线程永不过期
   *
   * @param name      线程池名称
   * @param threadNum 线程数量
   * @return /
   */
  public static ThreadPoolExecutor newFixedThreadPoolExecutor(int threadNum, String name) {
    return new ThreadPoolExecutor(threadNum, threadNum,
      0L, TimeUnit.MILLISECONDS,
      new LinkedBlockingQueue<>(), new DaemonThreadFactory(name));
  }

  /**
   * 创建一个线程池 当队列满之后，则阻塞调用线程直到队列为空
   *
   * @param name      线程池名称
   * @param threadNum 线程数量
   * @return /
   */
  public static ThreadPoolExecutor newBlockCallThreadPool(int threadNum, String name) {
    return new ThreadPoolExecutor(threadNum, threadNum,
      5L, TimeUnit.SECONDS,
      new SynchronousQueue<>(), new DaemonThreadFactory(name), BlockRejectHandler.me());
  }

  /**
   * 无限制线程数量的线程池
   *
   * @return /
   */
  public static ThreadPoolExecutor newCachedThreadPool(String name) {
    return new ThreadPoolExecutor(0, Integer.MAX_VALUE,
      60L, TimeUnit.SECONDS,
      new SynchronousQueue<>(), new DaemonThreadFactory(name));
  }

  /**
   * 获取当前JVM中运行的线程数量
   *
   * @return 线程数量
   */
  public static int getSystemActiveThreadCount() {
    ThreadGroup threadGroup = Thread.currentThread().getThreadGroup();
    while (threadGroup.getParent() != null) {
      threadGroup = threadGroup.getParent();
    }
    return threadGroup.activeCount();
  }
}
