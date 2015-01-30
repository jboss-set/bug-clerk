package org.jboss.jbossset.bugclerk.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.jboss.jbossset.bugclerk.Candidate;
import org.jboss.pull.shared.connectors.bugzilla.Bug;
import org.jboss.pull.shared.connectors.bugzilla.Comment;

public final class CollectionUtils {

    private CollectionUtils() {
    }

    @SuppressWarnings("rawtypes")
    public static <K, V> SortedSet getEntryOrEmptySet(K key, Map<K, SortedSet<V>> map) {
        return (map.containsKey(key) ? map.get(key) : new TreeSet<V>());
    }

    public static <T> Object[] objectsToArray(T... objects) {
        Object[] facts = { objects };
        return facts;
    }

    @SuppressWarnings("unchecked")
    public static List<Candidate> createCandidateList(Map<String, Bug> bugs, Map<String, SortedSet<Comment>> comments) {
        List<Candidate> candidates = new ArrayList<Candidate>(bugs.size());
        for (Bug bug : bugs.values())
            candidates.add(new Candidate(bug, CollectionUtils.getEntryOrEmptySet(String.valueOf(bug.getId()), comments)));
        return candidates;
    }
}
