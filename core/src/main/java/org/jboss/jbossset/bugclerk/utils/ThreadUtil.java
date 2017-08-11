package org.jboss.jbossset.bugclerk.utils;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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
            new IllegalStateException(e);
        } catch (ExecutionException e) {
            new IllegalStateException(e);
        } catch (TimeoutException e) {
            new IllegalStateException(e);
        } catch (Exception e) {
            new IllegalStateException(e);
        }
        return null;
    }
}
