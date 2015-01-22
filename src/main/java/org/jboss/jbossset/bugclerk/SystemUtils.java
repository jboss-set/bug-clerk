package org.jboss.jbossset.bugclerk;

public class SystemUtils {
    private static double UNIT_SHIFT = 1000.0;


    public static void println(Object object) {
        System.out.println(object);
    }

    public static double timeSpentInSecondsSince(long startTime) {
        return ((System.nanoTime() - startTime) / (UNIT_SHIFT * UNIT_SHIFT * UNIT_SHIFT));
    }


}
