package org.albianj.common.sync;

import org.albianj.AblServRouter;
import org.albianj.api.kernel.logger.LogLevel;

import java.util.concurrent.*;

public class ThreadPool {
    ThreadPoolExecutor executorPool = null;
    ThreadMonitor monitor = null;
    public ThreadPool(int coreNumbs,int maxNumbs,int keepAlive,int cacheQueueNumbs,int monitorInterval) {
        //RejectedExecutionHandler implementation
//        RejectedExecutionHandlerImpl rejectionHandler = new RejectedExecutionHandlerImpl();
        //Get the ThreadFactory implementation to use
        ThreadFactory threadFactory = Executors.defaultThreadFactory();

        //creating the ThreadPoolExecutor
        executorPool = new ThreadPoolExecutor(coreNumbs,maxNumbs,keepAlive ,TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(cacheQueueNumbs),
                threadFactory,
                new ThreadPoolExecutor.AbortPolicy());

        //start the monitoring thread
        monitor = new ThreadMonitor(executorPool, monitorInterval);
        Thread monitorThread = new Thread(monitor);
        monitorThread.setName("ThreadPoolMonitor");
        monitorThread.start();
    }

    public boolean addTask(Object sessionId,Runnable runnable){
        try {
            if(executorPool.isShutdown() || executorPool.isTerminated() || executorPool.isTerminating()){
                return false;
            }
            executorPool.execute(runnable);
            return true;
        }catch (RejectedExecutionException ree) {
            AblServRouter.log(sessionId, LogLevel.Error,ree,
                    "add task to threadpool is fail,maybe task is flow.");
            return false;
        }
    }

    public void shutdown(){
        try {
            executorPool.shutdown();
            //shut down the monitor thread
            Thread.sleep(5000);
            monitor.shutdown();
        }catch (Throwable t){

        }
    }
}
