package org.jboss.jbossset.bugclerk.checks.utils;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.List;

import org.jboss.jbossset.bugclerk.Candidate;
import org.jboss.jbossset.bugclerk.Violation;

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
        for ( Candidate candidate : candidates) {
            if ( candidate.getBug().getTrackerId().equals(bugId)) {
                List<Violation> violations = candidate.getViolations();
                assertThat(violations.size(), is(nbViolationExpected));
                for (Violation v : violations) {
                    assertThat(v.getCheckName(), is(checkname));
                }
            }
        }
    }
    
    public static void checkResults(Collection<Candidate> candidates, String bugId, int expectedNbOfViolations, String checkName) {
        boolean status = false;
        for ( Candidate candidate : candidates ) {
            String t = candidate.getBug().getTrackerId().get();
            if ( t.equals(bugId)) {
                assertTrue(checkViolationIsPresent(candidate.getViolations(), checkName)); //, is(true);
                assertThat(candidate.getViolations().size(), is(expectedNbOfViolations));
                status = true;
            }
        }
        if (! status ) fail("Expected violation data not found");       
    }
    
    public static boolean checkViolationIsPresent(List<Violation> violations, String checkName) {
        for ( Violation v : violations )
            if ( v.getCheckName().equals(checkName))
                return true;
        return false;
        
    }

}
