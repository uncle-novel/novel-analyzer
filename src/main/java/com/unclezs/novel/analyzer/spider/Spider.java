package com.unclezs.novel.analyzer.spider;

import com.unclezs.novel.analyzer.AnalyzerManager;
import com.unclezs.novel.analyzer.common.concurrent.ThreadUtils;
import com.unclezs.novel.analyzer.common.exception.SpiderRuntimeException;
import com.unclezs.novel.analyzer.core.model.AnalyzerRule;
import com.unclezs.novel.analyzer.model.Chapter;
import com.unclezs.novel.analyzer.model.ChapterState;
import com.unclezs.novel.analyzer.model.Novel;
import com.unclezs.novel.analyzer.spider.pipline.BaseFilePipeline;
import com.unclezs.novel.analyzer.spider.pipline.ConsolePipeline;
import com.unclezs.novel.analyzer.spider.pipline.Pipeline;
import com.unclezs.novel.analyzer.util.CollectionUtils;
import com.unclezs.novel.analyzer.util.FileUtils;
import com.unclezs.novel.analyzer.util.GsonUtils;
import com.unclezs.novel.analyzer.util.StringUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;

/**
 * 小说爬虫，提供了爬虫的一些能力
 * 提供管道处理爬虫爬取下来的章节数据
 * 支持多线程配置，默认懒加载，不执行run方法不会创建线程池
 * 配置都为懒加载，创建爬虫及创建线程池，都是在run方法执行时候初始化。
 * 可以对爬虫进行备份
 *
 * @author blog.unclezs.com
 * @date 2021/01/11 18:50
 */
@Slf4j
public final class Spider implements Serializable {
  public static final int INIT = 0;
  /**
   * 初始化完成，随时可以启动的状态
   */
  public static final int READY = 1;
  public static final int RUNNING = 2;
  public static final int PAUSING = 3;
  public static final int PAUSED = 4;
  public static final int STOPPING = 5;
  public static final int STOPPED = 6;
  /**
   * 抓取结束,不一定全部成功，结合COMPLETED判断是否完成
   */
  public static final int FINISHED = 7;
  public static final int COMPLETED = 8;
  /**
   * 爬虫计数器，统计本次启动了多少个爬虫
   */
  private static final AtomicInteger COUNTER = new AtomicInteger(1);
  /**
   * 爬虫状态 0：未开始  1：运行中  2：暂停中 3：已暂停  4：停止中 5：已停止
   * 可能3->1， 不可能4->1
   */
  private final transient AtomicInteger state = new AtomicInteger();
  /**
   * 实际的爬虫
   */
  @Getter
  private transient NovelSpider novelSpider;
  /**
   * 线程池
   */
  private transient ThreadPoolExecutor threadPool;
  /**
   * 进度改变处理器，用户可以通过此得知爬虫进度
   */
  private transient BiConsumer<Double, String> progressChangeHandler;
  /**
   * 数据处理管道
   */
  private transient List<Pipeline> pipelines = new ArrayList<>();
  /**
   * 正在执行的任务集合，用户监控任务状态
   *
   * @see com.unclezs.novel.analyzer.spider.Spider.Task
   */
  @Getter
  private transient Set<Task> tasks;
  /**
   * 线程数量
   */
  @Getter
  private int threadNum = 1;
  /**
   * 解析配置
   */
  @Getter
  private AnalyzerRule analyzerRule;
  /**
   * 重新下载 下载失败的章节 次数 0则不重试
   */
  @Getter
  @Setter
  private int currentTimes = 0;
  @Getter
  private int retryTimes = 0;
  /**
   * 小说目录
   */
  @Getter
  private List<Chapter> toc;
  /**
   * 小说信息
   */
  @Getter
  private Novel novel;
  /**
   * 小说目录地址
   */
  @Getter
  private String url;
  /**
   * 剩余未下载成功的章节
   */
  private AtomicInteger leftCount;
  /**
   * 失败的章节
   */
  private AtomicInteger errorCount;
  /**
   * 文件下载路径（如果有，用于dump时保留）
   */
  @Getter
  private String savePath;
  /**
   * 当状态改变时回调
   */
  private transient BiConsumer<Integer, Integer> onStateChange;

  /**
   * 创建一个爬虫
   *
   * @param url 目录URL链接
   * @return this
   */
  public static Spider create(String url) {
    return create().url(url);
  }

  /**
   * 从备份数据创建一个爬虫
   * 需要手动重新配置 进度监听、pipeline
   *
   * @param backup 备份JSON数据
   * @return this
   */
  public static Spider load(String backup) {
    return GsonUtils.parse(backup, Spider.class);
  }

  /**
   * 从备份数据文件 创建一个爬虫
   * 需要手动重新配置 进度监听、pipeline
   *
   * @param backupFilePath 备份json数据文件全路径
   * @return this
   * @throws java.io.IOException 文件不存在
   */
  public static Spider loadFromFile(String backupFilePath) throws IOException {
    return load(FileUtils.readUtf8String(backupFilePath));
  }

  /**
   * 创建一个爬虫
   *
   * @return this
   */
  public static Spider create() {
    return new Spider();
  }

  /**
   * 设置目录URL
   *
   * @param url 目录链接地址
   * @return this
   */
  public Spider url(String url) {
    this.url = url;
    return this;
  }

  /**
   * 设置小说信息
   *
   * @param novel 小说
   * @return this
   */
  public Spider novel(Novel novel) {
    this.novel = novel;
    return this;
  }

  /**
   * 设置小说下载路径，如果用了下载管道
   *
   * @param savePath 小说下载路径
   * @return this
   */
  public Spider savePath(String savePath) {
    this.savePath = savePath;
    return this;
  }

  /**
   * 设置小说信息
   *
   * @param onStateChange <以前的状态,新的状态> 状态改变回调
   * @return this
   */
  public Spider onStateChange(BiConsumer<Integer, Integer> onStateChange) {
    this.onStateChange = onStateChange;
    return this;
  }

  /**
   * 获取爬虫状态
   *
   * @return /
   */
  public int state() {
    return this.state.get();
  }

  /**
   * 监控当进度改变的时候
   *
   * @param changeHandler 任务进度处理器
   */
  public Spider progressChangeHandler(BiConsumer<Double, String> changeHandler) {
    this.progressChangeHandler = changeHandler;
    return this;
  }

  /**
   * 线程数量
   *
   * @param threadNum 线程数量
   * @return this
   */
  public Spider thread(int threadNum) {
    this.threadNum = threadNum;
    if (threadPool != null) {
      threadPool.setCorePoolSize(threadNum);
      threadPool.setMaximumPoolSize(threadNum);
    }
    return this;
  }

  /**
   * 数据处理管道
   *
   * @param pipeline 管道
   * @return this
   */
  public Spider pipeline(Pipeline pipeline) {
    this.pipelines.add(pipeline);
    return this;
  }

  /**
   * 数据处理管道
   *
   * @param pipelines 管道
   * @return this
   */
  public Spider pipelines(List<Pipeline> pipelines) {
    this.pipelines.addAll(pipelines);
    return this;
  }

  /**
   * 解析配置
   *
   * @param analyzerRule 配置
   * @return this
   */
  public Spider rule(AnalyzerRule analyzerRule) {
    this.analyzerRule = analyzerRule;
    return this;
  }

  /**
   * 设置 重新下载 下载失败的章节 次数
   *
   * @param retryTimes 次数 0则不重试
   * @return this
   */
  public Spider retryTimes(int retryTimes) {
    this.retryTimes = retryTimes;
    return this;
  }

  /**
   * 初始化爬虫，兼容从备份导入方式
   */
  private void init() throws IOException {
    setState(INIT);
    novelSpider = new NovelSpider();
    // 设置解析配置
    if (this.analyzerRule == null) {
      throw new SpiderRuntimeException("解析规则不能为空");
    }
    novelSpider.setRule(this.analyzerRule);
    if (threadNum < 1) {
      log.trace("线程数量小于1，自动重置为1");
      this.threadNum = 1;
    }
    if (StringUtils.isBlank(url)) {
      throw new SpiderRuntimeException("目录地址不能为空");
    }
    // 小说信息
    if (novel == null) {
      novel = novelSpider.details(url);
      novel.setUrl(url);
      // 获取小说详情信息
      log.trace("抓取到小说详情信息：{}", novel);
    }
    // 章节抓取 兼容备份恢复情况
    if (CollectionUtils.isEmpty(this.toc)) {
      // 如果小说信息里面已经有了章节直接使用
      if (CollectionUtils.isNotEmpty(novel.getChapters())) {
        this.toc = novel.getChapters();
      } else {
        toc = novelSpider.toc(url);
      }
      if (CollectionUtils.isEmpty(this.toc)) {
        log.warn("章节数据抓取失败或未获取到章节：{}", url);
        throw new SpiderRuntimeException("章节数据抓取失败或未获取到章节:" + url);
      }
      leftCount = new AtomicInteger(this.toc.size());
    }
    // 错误章节数量
    if (errorCount == null) {
      errorCount = new AtomicInteger(0);
    }
    // 反序列化时候也要设置
    novel.setChapters(toc);
    // 确认章节数据正确抓取后再初始化线程
    if (threadPool == null) {
      threadPool = ThreadUtils.newFixedThreadPoolExecutor(this.threadNum, String.format("spider-%d", COUNTER.getAndIncrement()));
    }
    // 没有提供管道则使用控制台打印
    if (pipelines.isEmpty()) {
      pipelines.add(new ConsolePipeline());
    } else {
      // 设置文件保存路径
      for (Pipeline pipeline : pipelines) {
        if (pipeline instanceof BaseFilePipeline && StringUtils.isNotBlank(savePath)) {
          ((BaseFilePipeline) pipeline).setPath(savePath);
        }
      }
    }
    // 小说信息注入到管道
    pipelines.forEach(pipeline -> pipeline.injectNovel(novel));
    // 初始化任务监控集合
    tasks = new HashSet<>(toc.size() * 2);
    setState(READY);
  }

  /**
   * 启动爬虫
   *
   * @throws java.io.IOException 章节数据抓取失败
   */
  public synchronized void run() throws IOException {
    if (isState(INIT)) {
      init();
    }
    do {
      this.crawling();
      currentTimes++;
    } while (canRetry());
    // 没有全部成功，并且不是中断，标记为已经结束状态
    if (!isCompleted() && tasks.isEmpty()) {
      setState(FINISHED);
    }
    currentTimes = 0;
  }

  /**
   * 异步启动爬虫
   */
  public void runAsync() {
    if (isState(RUNNING)) {
      return;
    }
    ThreadUtils.execute(() -> {
      try {
        run();
      } catch (IOException e) {
        log.error("启动失败爬虫失败", e);
      }
    });
  }

  /**
   * 爬取一本小说
   */
  private void crawling() {
    // 只有 已暂停和准备状态可以启动爬虫
    if (isState(READY) || isState(PAUSED) || isState(FINISHED)) {
      setState(RUNNING);
      log.debug("开始爬取小说[{}]：剩余未下载{}/{}章 开启{}个线程 是否启用自动代理：{}", novel.getTitle(), leftCount, toc.size(), threadNum, AnalyzerManager.me().isAutoProxy());
      // 清空以前的任务
      tasks.clear();
      toc.stream()
        .filter(chapter -> !chapter.downloaded())
        .forEach(chapter -> threadPool.execute(new Task(chapter)));
      // 全部任务已完成（不一定都下载成功）
      while (!tasks.isEmpty()) {
        // 被暂停
        if (isState(PAUSING)) {
          setState(PAUSING, PAUSED);
          return;
        }
        // 被停止
        if (isState(STOPPING)) {
          setState(STOPPING, STOPPED);
          return;
        }
      }
      // 全部章节都已经下载成功
      if (isCompleted()) {
        setState(COMPLETED);
      }
    } else {
      log.debug("爬虫状态已经进入不可启动状态 非READY与PAUSED状态");
    }
  }

  /**
   * 是否全部下载成功
   *
   * @return true 全部都已经下成功
   */
  public boolean isCompleted() {
    return leftCount.get() == 0;
  }

  /**
   * 是否能够继续重试
   *
   * @return true 可以重试
   */
  public boolean canRetry() {
    return currentTimes <= retryTimes && !isCompleted() && tasks.isEmpty();
  }

  /**
   * 设置爬虫状态
   *
   * @param expectState 状态
   * @param toState     要设置的状态
   */
  private void setState(Integer expectState, int toState) {
    if (isState(INIT) && RUNNING != toState && toState != READY) {
      log.warn("小说爬虫未启动，请先启动爬虫，不然无需其他操作.");
      return;
    }
    beforeChangeState(toState);
    // 状态改变回调
    if (onStateChange != null) {
      onStateChange.accept(expectState, toState);
    }
    if (expectState != null && isState(expectState)) {
      this.state.compareAndSet(expectState, toState);
    } else {
      this.state.set(toState);
    }
  }

  /**
   * 设置状态
   *
   * @param state 状态
   */
  private void setState(int state) {
    setState(null, state);
  }

  /**
   * 根据状态采取一定行为
   *
   * @param state 状态
   */
  private void beforeChangeState(int state) {
    switch (state) {
      case PAUSING:
        log.trace("小说[{}]抓取暂停中：剩余未下载{}/{}章", novel.getTitle(), leftCount.get(), toc.size());
        cancelRunningTasks();
        break;
      case PAUSED:
        log.trace("小说[{}]抓取已经暂停：剩余未下载{}/{}章", novel.getTitle(), leftCount.get(), toc.size());
        break;
      // 暂停、停止、清除全部正在自行的任务
      case STOPPING:
        log.trace("小说[{}]抓取停止中：剩余未下载{}/{}章", novel.getTitle(), leftCount.get(), toc.size());
        cancelRunningTasks();
        break;
      // 停止、完成 关闭线程池
      case STOPPED:
        log.trace("小说[{}]抓取已停止 - 任务丢弃", novel.getTitle());
        shutdown();
        break;
      // 停止、完成 关闭线程池
      case FINISHED:
        log.trace("小说[{}]抓取完成：剩余未下载{}/{}章，错误章节：{}", novel.getTitle(), leftCount.get(), toc.size(), errorCount());
        break;
      case COMPLETED:
        log.debug("小说[{}]抓取成功：共{}章", novel.getTitle(), toc.size());
        // 回调管道处理完成
        pipelines.forEach(Pipeline::onComplete);
        shutdown();
        break;
      default:
        break;
    }
  }

  /**
   * 判断是否为某个状态
   *
   * @param state 目标状态
   * @return /
   */
  public boolean isState(int state) {
    return state() == state;
  }

  /**
   * 取消正在执行的任务
   */
  public void cancelRunningTasks() {
    tasks.forEach(Task::cancel);
    threadPool.getQueue().clear();
  }

  /**
   * 关闭爬虫，释放一些重资源
   */
  private void shutdown() {
    this.cancelRunningTasks();
    this.threadPool.shutdown();
    this.toc = null;
    this.novel = null;
    this.threadPool = null;
    this.pipelines = null;
    this.progressChangeHandler = null;
  }

  /**
   * 暂停爬虫，可以重新启动，不会立即暂停，进行的任务还是会进行
   */
  public void pause() {
    setState(RUNNING, PAUSING);
  }

  /**
   * 停止爬虫 将不会接收新的任务
   * 不会立即停止，进行的任务还是会进行
   */
  public void stop() {
    if (isState(RUNNING)) {
      setState(STOPPING);
    } else if (state() < RUNNING) {
      setState(STOPPED);
    }
  }

  /**
   * 任务进度
   *
   * @return 任务进度百分比
   */
  public double progress() {
    double progress = 0d;
    if (toc != null && leftCount != null) {
      progress = (1 - (double) leftCount.get() / toc.size());
    }
    return progress;
  }

  /**
   * 剩余未下载成功的数量
   *
   * @return 剩余数量
   */
  public int leftCount() {
    if (leftCount == null) {
      if (toc != null) {
        return toc.size();
      }
      if (novel.getChapters() != null) {
        return novel.getChapters().size();
      }
      return 0;
    }
    return leftCount.get();
  }

  /**
   * 错误章节数量
   *
   * @return 数量
   */
  public int errorCount() {
    if (errorCount == null) {
      return 0;
    }
    return errorCount.get();
  }

  /**
   * 备份爬虫，持久化存储数据到文件，可以用于恢复爬虫
   */
  public String dump() {
    this.pause();
    return GsonUtils.toJson(this);
  }

  /**
   * 备份爬虫，持久化存储数据到文件，可以用于恢复爬虫
   *
   * @param filePath 备份文件全路径
   */
  public void dump(String filePath) throws IOException {
    if (novel == null) {
      return;
    }
    if (StringUtils.isBlank(filePath)) {
      log.error("备份文件路径不能为空：{}.", novel.getTitle());
      return;
    }
    FileUtils.deleteFile(filePath);
    FileUtils.writeUtf8String(filePath, dump());
    log.trace("备份爬虫完成 - {} - 到：{}.", novel.getTitle(), filePath);
  }

  /**
   * 章节抓取任务
   */
  class Task implements Runnable {
    private final Chapter chapter;
    private final AtomicBoolean canceled = new AtomicBoolean(false);

    public Task(Chapter chapter) {
      this.chapter = chapter;
      tasks.add(this);
    }

    /**
     * 执行章节抓取任务
     */
    @Override
    public void run() {
      try {
        if (canceled.compareAndSet(false, false)) {
          String content = novelSpider.content(chapter.getUrl());
          // 内容是空白也当做错误处理
          if (StringUtils.isBlank(content)) {
            throw new SpiderRuntimeException("未知的，未抓取的章节内容");
          }
          chapter.setContent(content);
          if (canceled.compareAndSet(false, false)) {
            // 管道处理章节数据
            if (CollectionUtils.isNotEmpty(pipelines)) {
              pipelines.forEach(pipeline -> pipeline.process(chapter));
            }
            // 管道处理完成之后释放章节内容
            chapter.setContent(null);
            // 计数器改变
            leftCount.getAndDecrement();
            if (chapter.getState() == ChapterState.FAILED) {
              errorCount.decrementAndGet();
            }
            // 下完完成标记
            chapter.setState(ChapterState.DOWNLOADED);
            // 通知完成一个章节的抓取
            if (progressChangeHandler != null && toc != null) {
              int total = toc.size();
              progressChangeHandler.accept(progress(), String.format("%d/%d", (total - leftCount.get()), total));
            }
          }
        }
      } catch (Exception e) {
        if (chapter.getState() != ChapterState.FAILED) {
          chapter.setState(ChapterState.FAILED);
          errorCount.incrementAndGet();
        }
        chapter.setMsg(e.getMessage());
        log.warn("小说章节内容爬取失败：order:{} - {} - {}", chapter.getOrder(), chapter.getName(), chapter.getUrl(), e);
      } finally {
        tasks.remove(this);
      }
    }

    /**
     * 尝试取消任务 不一定会有效，在正文解析前取消有效
     */
    public void cancel() {
      this.canceled.set(true);
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      Task task = (Task) o;
      return chapter.getOrder() == task.chapter.getOrder();
    }

    @Override
    public int hashCode() {
      return Objects.hash(chapter);
    }
  }
}
