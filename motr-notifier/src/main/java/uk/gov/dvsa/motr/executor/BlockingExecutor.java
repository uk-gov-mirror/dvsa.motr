package uk.gov.dvsa.motr.executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class BlockingExecutor extends ThreadPoolExecutor {

    private static final Logger logger = LoggerFactory.getLogger(BlockingExecutor.class);

    private final Semaphore semaphore;

    public BlockingExecutor(final int workerCount) {

        super(workerCount, workerCount, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
        semaphore = new Semaphore(workerCount);
    }

    @Override
    public void execute(final Runnable task) {

        boolean acquired = false;

        do {
            try {
                semaphore.acquire();
                acquired = true;
            } catch (final InterruptedException e) {
                logger.warn("InterruptedException whilst aquiring semaphore", e);

                // Restore interrupted state...
                Thread.currentThread().interrupt();
            }
        } while (!acquired);

        try {
            super.execute(task);
        } catch (final RejectedExecutionException e) {
            semaphore.release();
            throw e;
        }
    }

    @Override
    protected void afterExecute(final Runnable r, final Throwable t) {

        super.afterExecute(r, t);
        semaphore.release();
    }
}
