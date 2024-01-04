package kpi.com.threadPool;

import java.util.LinkedList;

public class ThreadPool {
    private final LinkedList<Runnable> taskQueue;

    public ThreadPool(int poolSize) {
        this.taskQueue = new LinkedList<>();
        WorkerThread[] workers = new WorkerThread[poolSize];

        for (int i = 0; i < poolSize; i++) {
            workers[i] = new WorkerThread();
            workers[i].start();
        }
    }

    public void submit(Runnable task) {
        synchronized (taskQueue) {
            taskQueue.addLast(task);
            taskQueue.notify();
        }
    }

    public static void main(String[] args) {
        ThreadPool threadPool = new ThreadPool(5);

        for (int i = 0; i < 1000; i++) {
            final int taskNumber = i;
            threadPool.submit(() -> System.out.println("Task " + taskNumber + " executed by thread " +
                    Thread.currentThread().getName()));
        }
    }

    private class WorkerThread extends Thread {
        @Override
        public void run() {
            Runnable task;

            while (true) {
                synchronized (taskQueue) {
                    while (taskQueue.isEmpty()) {
                        try {
                            taskQueue.wait();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            return;
                        }
                    }
                    task = taskQueue.removeFirst();
                }

                try {
                    task.run();
                } catch (RuntimeException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
