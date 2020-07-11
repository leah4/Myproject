package task;

import java.io.File;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class FileScanner {

    //1.核心线程数：始终运行的线程数量
    //2.最大的线程数：有新任务并且当前运行的线程数小于最大线程数，会创建新的线程来处理任务
    //3-4.超过3这个数量的4的之恶个时间单位 2-1 数量的线程就会关闭
    //5.工作的阻塞队列
    //6.如果超出工作队列的长度，任务的处理方式
//    private ThreadPoolExecutor pool = new ThreadPoolExecutor(
//            3, 3, 0, TimeUnit.MICROSECONDS,
//            new LinkedBlockingDeque<>(), new ThreadPoolExecutor.AbortPolicy()
//    );
    //之前多线程讲解的方法是一种快捷创建方式
    //省略的方法快捷创建多线程
    private ExecutorService pool = Executors.newFixedThreadPool(4);

    //计数器，不传入数值，表示初始化的值0
    private volatile AtomicInteger count = new AtomicInteger();

    //线程等待的锁对象
    private Object lock = new Object(); //第一种sychronized进行wait
    private CountDownLatch latch = new CountDownLatch(1);//第二种await阻塞等待 知道latch等于0
    private Semaphore semaphore = new Semaphore(0);//第三种acquire阻塞等待一定数量的许可

    private ScanCallBack callback;

    public FileScanner(ScanCallBack callBack) {
        this.callback = callBack;
    }

    /**
     *  扫描文件夹中的子文件和子文件夹
     * @param path
     */
    public  void scan(String path) {
        count.incrementAndGet(); //启动根目录扫描任务 计数器++i
        doScan(new File(path));
    }

    /**
     * @param dir 待处理文件夹
     */
    private void doScan(File dir){
        pool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    callback.callback(dir);  //文件保存操作
                    File[] children = dir.listFiles();  //下一级文件和文件夹
                    if (children != null) {
                        for (File child : children) {
                            if (child.isDirectory()) { //如果是文件夹，递归处理
                                count.incrementAndGet();//启动文件夹扫描任务 计数器++i
                                System.out.println("当前任务数：" + count.get());
                                doScan(child);
                            }
                        }
                    }
                } finally { //不管是否出现异常都能执行finally里的计数器-1操作
                    int r = count.decrementAndGet(); //--i
                    if (r == 0) {
//                        synchronized (lock) {
//                            lock.notify();
//                        }
                        //latch.countDown();
                        semaphore.release();
                    }
                }
            }
        });
    }

    /**
     * 等待扫描任务结束
     * 多线程任务等待的方式：
     * 1.join（）:需要线程Thread 类的引用对象来调用
     * 2.wait()线程间的等待
     */
    public void waitFinish() throws InterruptedException {
//        synchronized (lock){
//            lock.wait();
//        }
       // latch.await();
        try {
            semaphore.acquire();
        } finally {
            //阻塞等待直到任务完成，完成后关闭线程池
            System.out.println("关闭线程池...");
            //内部原理：通过内部的Thread.interrupt来中断
            //   pool.shutdown(); //建议使用
            pool.shutdownNow();
        }
    }

}
