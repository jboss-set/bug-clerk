package org.jboss.jbossset.bugclerk.checks.utils;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Collection;

import org.jboss.jbossset.bugclerk.Violation;

public final class AssertsHelper {

    private AssertsHelper() {
    }

    public static void assertResultsIsAsExpected(Collection<Violation> violations, String checkname, String bugId) {
        assertResultsIsAsExpected(violations, checkname, bugId, 1);
    }

    public static void assertOneViolationFound(Collection<Violation> violations, String checkname, String bugId) {
        assertResultsIsAsExpected(violations, checkname, bugId, 1);
    }

    public static void assertNoViolationFound(Collection<Violation> violations, String checkname, String bugId) {
        assertResultsIsAsExpected(violations, checkname, bugId, 0);
    }

    public static final String BUGZILLA_TRACKER_ID_PREFIX = "https://bugzilla.redhat.com/show_bug.cgi?id=";

    public static void assertResultsIsAsExpected(Collection<Violation> violations, String checkname, String bugId,
            int nbViolationExpected) {
        assertThat(violations.size(), is(nbViolationExpected));
        for (Violation v : violations) {
            assertThat(v.getCandidate().getBug().getTrackerId().get(), is(bugId));
            assertThat(v.getCheckName(), is(checkname));
        }
    }
}
