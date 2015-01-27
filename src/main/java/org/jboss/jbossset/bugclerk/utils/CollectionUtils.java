package org.jboss.jbossset.bugclerk.utils;

import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

public final class CollectionUtils {

    private CollectionUtils() {}

    @SuppressWarnings("rawtypes")
    public static <K,V> SortedSet getEntryOrEmptySet(K key, Map<K, SortedSet<V>> map) {
        return (map.containsKey(key) ? map.get(key) : new TreeSet<V>());
    }

}
