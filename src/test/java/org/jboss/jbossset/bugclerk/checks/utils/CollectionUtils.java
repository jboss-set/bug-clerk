package org.jboss.jbossset.bugclerk.checks.utils;

import java.util.HashSet;
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
}
