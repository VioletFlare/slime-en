package com.github.nekolr.slime.concurrent;

import com.github.nekolr.slime.constant.Constants;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Threads
 */
public class SpiderThreadPoolExecutor {

    /**
     * Maximum number of threads in thread pool
     */
    private int maxThreads;

    /**
     * Thread lifespan，Seconds
     */
    private final long keepAliveTime = 10;

    /**
     * Priority to actually running threads
     */
    private final ThreadPoolExecutor threadPoolExecutor;

    /**
     * Thread number，From 1 Start
     */
    private final AtomicInteger threadNumber = new AtomicInteger(1);

    /**
     * Thread name prefix
     */
    private final String SLIME_THREAD_NAME_PREFIX = Constants.SLIME_THREAD_NAME_PREFIX;

    /**
     * Thread Group
     */
    private static final ThreadGroup SLIME_THREAD_GROUP = new ThreadGroup(Constants.SLIME_THREAD_GROUP_NAME);

    /**
     * Thread pool creator
     *
     * @param maxThreads Maximum number of threads
     */
    public SpiderThreadPoolExecutor(int maxThreads) {
        this.maxThreads = maxThreads;
        this.threadPoolExecutor = new ThreadPoolExecutor(
                maxThreads,
                maxThreads,
                keepAliveTime,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(),
                // Rewriting thread group and thread name
                r -> new Thread(SLIME_THREAD_GROUP, r, SLIME_THREAD_NAME_PREFIX + threadNumber.getAndIncrement())
        );
    }

    /**
     * Submit tasks directly to the parent thread pool
     *
     * @param runnable Tasks
     * @return Tasks
     */
    public Future<?> submit(Runnable runnable) {
        return threadPoolExecutor.submit(runnable);
    }

    /**
     * Create child thread pool
     *
     * @param threads Maximum number of threads in thread pool
     * @return Threads
     */
    public SubThreadPoolExecutor createSubThreadPoolExecutor(int threads) {
        return new SubThreadPoolExecutor(Math.min(this.maxThreads, threads));
    }

    /**
     * Threads
     */
    public class SubThreadPoolExecutor {

        /**
         * Maximum number of threads in thread pool
         */
        private int threads;

        /**
         * In progress（Submit Task to Queue）List，Thread count limit
         */
        private Future<?>[] tasks;

        /**
         * The amount of time after which the assistant will automatically answer incoming calls.
         */
        private final long waitTaskTimeout = 10;

        /**
         * Is the child pool running
         */
        private volatile boolean running = true;

        /**
         * Is the submit thread still running
         */
        private volatile boolean submitting = false;

        /**
         * Completed Task Count
         */
        private AtomicInteger executingTaskNumber = new AtomicInteger(0);

        /**
         * Candidate Task List
         */
        private LinkedBlockingQueue<FutureTask<?>> candidateTaskQueue;

        /**
         * ThreadFactory
         *
         * @param threads Thread pool size
         */
        public SubThreadPoolExecutor(int threads) {
            this.threads = threads;
            this.tasks = new Future[threads];
            this.candidateTaskQueue = new LinkedBlockingQueue<>();
        }

        /**
         * Get the current list of shadows
         *
         * @return -1 Indicates all threads in the thread pool are busy，No free threads available
         */
        private int index() {
            for (int i = 0; i < threads; i++) {
                if (tasks[i] == null || tasks[i].isDone()) {
                    return i;
                }
            }
            return -1;
        }

        /**
         * Delete completed tasks
         */
        private void removeDoneFuture() {
            for (int i = 0; i < threads; i++) {
                try {
                    // Please wait until the task is complete
                    if (tasks[i] != null && tasks[i].get(waitTaskTimeout, TimeUnit.MILLISECONDS) == null) {
                        tasks[i] = null;
                    }
                } catch (Throwable ignored) {
                }
            }
        }

        /**
         * Waiting for free threads
         */
        private void await() {
            while (index() == -1) {
                removeDoneFuture();
            }
        }

        /**
         * Wait for all threads to finish
         */
        public void awaitTermination() {
            // Wait for all threads to finish
            // Attention required：One of the threads in the thread pool is the dispatcher thread.，This thread will be made available after all of the tasks in this flow have completed
            while (executingTaskNumber.get() > 1) {
                removeDoneFuture();
            }
            running = false;
            // Wakeup call，Make the submit thread quit running
            synchronized (candidateTaskQueue) {
                candidateTaskQueue.notifyAll();
            }
        }

        /**
         * Special thanks to:
         *
         * @param runnable Tasks
         * @param value    Return Value
         * @param <T>      Return Value Type
         * @return Tasks
         */
        public <T> Future<T> submitAsync(Runnable runnable, T value) {
            FutureTask<T> futureTask = new FutureTask<>(() -> {
                try {
                    // Number of currently running tasks +1
                    executingTaskNumber.incrementAndGet();
                    // 执行任务
                    runnable.run();
                } finally {
                    // Number of currently running tasks -1
                    executingTaskNumber.decrementAndGet();
                }
            }, value);
            // Add the task to the candidate task list
            candidateTaskQueue.add(futureTask);
            // The first time you call，Submit thread not started yet，Start Upload Thread
            if (!submitting) {
                submitting = true;
                CompletableFuture.runAsync(this::submit);
            }
            // Notify that the task has been received from the scheduler and is now in the thread pool
            synchronized (candidateTaskQueue) {
                candidateTaskQueue.notifyAll();
            }
            return futureTask;
        }

        /**
         * Submit Task to Queue
         */
        private void submit() {
            while (running) {
                synchronized (candidateTaskQueue) {
                    try {
                        // If the task list is empty，Please wait while adding
                        if (candidateTaskQueue.isEmpty()) {
                            candidateTaskQueue.wait();
                        }
                        // When a submit thread is awoken，Get all the tasks from the candidate task list and submit them to the thread pool
                        while (!candidateTaskQueue.isEmpty()) {
                            FutureTask<?> futureTask = candidateTaskQueue.remove();
                            // Wait for a free thread
                            await();
                            // Attention required：Tasks submitted this way always return the value null
                            tasks[index()] = threadPoolExecutor.submit(futureTask);
                        }
                    } catch (InterruptedException ignored) {
                    }
                }
            }
        }
    }
}
