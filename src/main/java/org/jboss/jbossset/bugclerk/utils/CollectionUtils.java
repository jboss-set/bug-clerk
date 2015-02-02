package org.jboss.jbossset.bugclerk.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.jboss.jbossset.bugclerk.Candidate;
import org.jboss.jbossset.bugclerk.Violation;
import org.jboss.pull.shared.connectors.bugzilla.Bug;
import org.jboss.pull.shared.connectors.bugzilla.Comment;

public final class CollectionUtils {

    private CollectionUtils() {
    }

    @SuppressWarnings("rawtypes")
    public static <K, V> SortedSet getEntryOrEmptySet(K key, Map<K, SortedSet<V>> map) {
        return (map.containsKey(key) ? map.get(key) : new TreeSet<V>());
    }

    @SafeVarargs
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

    public static Map<Integer, List<Violation>> indexedViolationsByBugId(Collection<Violation> violations) {
        Map<Integer, List<Violation>> violationIndexedByBugId = new HashMap<Integer, List<Violation>>(violations.size());
        for (Violation violation : violations) {
            int id = violation.getBug().getId();
            if (!violationIndexedByBugId.containsKey(id)) {
                List<Violation> violationsForBug = new ArrayList<Violation>();
                violationsForBug.add(violation);
                violationIndexedByBugId.put(id, violationsForBug);
            } else
                violationIndexedByBugId.get(id).add(violation);
        }
        return violationIndexedByBugId;
    }

}
