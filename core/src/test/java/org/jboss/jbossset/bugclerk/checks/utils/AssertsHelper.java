package org.jboss.jbossset.bugclerk.checks.utils;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.List;

import org.jboss.jbossset.bugclerk.Candidate;
import org.jboss.set.aphrodite.domain.Violation;

public final class AssertsHelper {

    private AssertsHelper() {
    }

    public static void assertResultsIsAsExpected(Collection<Candidate> candidates, String checkname, String bugId) {
        assertResultsIsAsExpected(candidates, checkname, bugId, 1);
    }

    public static void assertOneViolationFound(Collection<Candidate> candidates, String checkname, String bugId) {
        assertResultsIsAsExpected(candidates, checkname, bugId, 1);
    }

    public static void assertNoViolationFound(Collection<Candidate> candidates, String checkname, String bugId) {
        assertResultsIsAsExpected(candidates, checkname, bugId, 0);
    }

    public static final String BUGZILLA_TRACKER_ID_PREFIX = "https://bugzilla.redhat.com/show_bug.cgi?id=";

    public static void assertResultsIsAsExpected(Collection<Candidate> candidates, String checkname, String bugId,
            int nbViolationExpected) {
        boolean status = false;
        for ( Candidate candidate : candidates) {
            if ( candidate.getBug().getTrackerId().get().equals(bugId)) {
                List<Violation> violations = candidate.getViolations();
                assertThat(violations.size(), is(nbViolationExpected));
                for (Violation v : violations) {
                    assertThat(v.getCheckName(), is(checkname));
                    status = true;
                }
            }
        }
        if (! status && nbViolationExpected > 0 ) fail("Expected violation data not found");
    }

    public static boolean checkViolationIsPresent(List<Violation> violations, String checkName) {
        for ( Violation v : violations )
            if ( v.getCheckName().equals(checkName))
                return true;
        return false;

    }

}
