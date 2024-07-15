package org.albianj.common.sync;

import org.albianj.AblServRouter;
import org.albianj.api.kernel.logger.LogLevel;

import java.util.concurrent.ThreadPoolExecutor;

public class ThreadMonitor implements Runnable {

    private ThreadPoolExecutor executor;

    private int seconds;

    private boolean run=true;

    public ThreadMonitor(ThreadPoolExecutor executor, int interval) {
        this.executor = executor;
        this.seconds= interval;
    }

    public void shutdown(){
        this.run = false;
    }

    @Override
    public void run()
    {
        while(run){
            AblServRouter.log("ThreadPool-Monitor", LogLevel.Mark,
                    String.format("[monitor] [%d/%d] Active: %d, Completed: %d, Task: %d, isShutdown: %s, isTerminated: %s",
                            this.executor.getPoolSize(),
                            this.executor.getCorePoolSize(),
                            this.executor.getActiveCount(),
                            this.executor.getCompletedTaskCount(),
                            this.executor.getTaskCount(),
                            this.executor.isShutdown(),
                            this.executor.isTerminated()));
            try {
                Thread.sleep(seconds * 1000);
            } catch (InterruptedException e) {

            }
        }

    }
}