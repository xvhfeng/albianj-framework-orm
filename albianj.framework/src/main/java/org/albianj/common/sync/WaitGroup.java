package org.albianj.common.sync;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class WaitGroup {
    private CountDownLatch latch = null;
    private List<Task> taks = null;
    private ThreadPool threadPool = null;
    public WaitGroup(int numbs,ThreadPool threadPool){
        this.latch = new CountDownLatch(numbs);
        this.taks = new ArrayList<>(numbs);
        this.threadPool = threadPool;
    }

    public void addTask(Runnable runnable){
        taks.add(new Task(this.latch,runnable));
    }

    public void bgnAndWait(String sessionId){
        try {
            for (Task t : taks) {
                this.threadPool.addTask(sessionId, t);
            }
            latch.await();
        }catch (Exception e){

        }
    }

    static class Task implements Runnable {
        private  CountDownLatch latch;
        private Runnable task;

        public Task(CountDownLatch latch,Runnable runnable) {
            this.latch = latch;
            this.task = runnable;
        }

        @Override
        public void run() {
            try {
                this.task.run();
            } catch (Exception e) {

            }finally {
                // 通知CountDownLatch，一个goroutine已经完成
                latch.countDown();
            }
        }
    }
}
