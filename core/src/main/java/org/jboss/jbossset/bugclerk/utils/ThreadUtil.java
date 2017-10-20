package org.jboss.jbossset.bugclerk.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;

public final class ThreadUtil {

    private ThreadUtil() {
    }

    private static final int DEFAULT_TIME_OUT = 300;

    public static <V> V execute(Callable<V> task) {
        try {
            FutureTask<V> timeoutTask = new FutureTask<V>(task);
            new Thread(timeoutTask).start();
            return timeoutTask.get(DEFAULT_TIME_OUT, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            LoggingUtils.getLogger().log(Level.WARNING,"Callable interrupted.", e);
        } catch (ExecutionException e) {
            LoggingUtils.getLogger().log(Level.WARNING,"Callable failed.", e);
        } catch (TimeoutException e) {
            LoggingUtils.getLogger().log(Level.WARNING,"Callable time out.", e);
        }
        return null;
    }

    public static <V> List<V> executeParallel(List<Callable<V>> tasks, ExecutorService executorService) {
        try {
            List<Future<V>> futures = executorService.invokeAll(tasks, DEFAULT_TIME_OUT, TimeUnit.SECONDS);
            List<V> results = new ArrayList<>(futures.size());
            for (Future<V> future: futures) {
                if (future.isDone() && !future.isCancelled()) {
                    try {
                        V result = future.get();
                        if (result != null) {
                            results.add(result);
                        }
                    } catch (InterruptedException | ExecutionException ignore) {
                        // futures are already completed
                    }
                }
            }
            return results;
        } catch (InterruptedException e) {
            LoggingUtils.getLogger().log(Level.WARNING,"Callable interrupted.", e);
            return Collections.emptyList();
        }
    }
}
