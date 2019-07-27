package com.recordrack.db;

import org.json.JSONArray;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * This class creates a single thread executor service which handles all requests to the database.
 */
public class DatabaseThread
{
    private static DatabaseThread thread;
    private static ExecutorService executorService;
    public static DatabaseThread getInstance()
    {
        if(thread == null)
        {
            thread = new DatabaseThread();
            executorService = Executors.newSingleThreadExecutor();
        }

        return thread;
    }

    public void postTask(Runnable run)
    {
       executorService.submit(run);
    }

    public void shutdown()
    {
        executorService.shutdown();
    }
}
