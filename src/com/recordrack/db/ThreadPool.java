package com.recordrack.db;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Class for creating a pool of threads for concurrency
 */
public class ThreadPool
{
    private static ThreadPool threadPool = null;
    private static ExecutorService executorService = null;
    public static ThreadPool getInstance()
    {
        if(threadPool == null)
        {
            threadPool = new ThreadPool();
            executorService = Executors.newFixedThreadPool(10);
        }
        return threadPool;
    }

    /**
     * method called to the queue of the executorservice
     * @param run runnable to be executed
     */
    public void postTask(Runnable run)
    {
        executorService.submit(run);
    }

    /**
     * method called when the main stage is closed to shutdown the executorservice and destroy threads
     */
    public void shutdown()
    {
        executorService.shutdown();
    }
}
