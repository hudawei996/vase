package com.wanjian.proxy;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by wanjian on 2017/7/3.
 */

public class ThreadPool {
    private static ThreadPoolExecutor service = new ThreadPoolExecutor(100, Integer.MAX_VALUE, 60, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(1));

    static {
        service.prestartAllCoreThreads();
    }

    public static void submit(Runnable runnable) {
        service.execute(runnable);
    }
}
