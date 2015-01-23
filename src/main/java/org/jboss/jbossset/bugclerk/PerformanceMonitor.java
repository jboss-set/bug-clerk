package org.jboss.jbossset.bugclerk;

public class PerformanceMonitor {

    private long startTime = System.currentTimeMillis();

    private static double UNIT_SHIFT = 1000.0;

    public void startClock() {
        startTime = System.currentTimeMillis();
    }

    public double returnsTimeElapsedAndRestartClock() {
        double timeElapses = timeSpentInSecondsSince();
        startClock();
        return timeElapses;
    }

    public double timeSpentInSecondsSince() {
        return ((System.nanoTime() - startTime) / (UNIT_SHIFT * UNIT_SHIFT * UNIT_SHIFT));
    }

}
