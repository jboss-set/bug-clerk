package org.jboss.jbossset.bugclerk.checks.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class CollectionUtils {

    private CollectionUtils() {
    }

    @SafeVarargs
    public static <T> Set<T> asSetOf(T... items) {
        Set<T> releasesSet = new HashSet<T>();
        for (T item : items)
            releasesSet.add(item);
        return releasesSet;
    }

    @SafeVarargs
    public static <T> List<T> asListOf(T... items) {
        List<T> list = new ArrayList<T>(items.length);
        for ( T item : items) {
            list.add(item);
        }
        return list;
    }
}
