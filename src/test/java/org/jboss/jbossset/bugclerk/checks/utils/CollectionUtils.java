package org.jboss.jbossset.bugclerk.checks.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jboss.jbossset.bugclerk.Candidate;
import org.jboss.set.aphrodite.domain.Issue;

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

    @SafeVarargs
    public static final Collection<Candidate> buildCollectionOfCandidates(Issue... bugs) {
        Collection<Candidate> mocks = new ArrayList<Candidate>(bugs.length);
        for (Issue bug : bugs) {
            mocks.add(new Candidate(bug));
        }
        return mocks;
    }
}
