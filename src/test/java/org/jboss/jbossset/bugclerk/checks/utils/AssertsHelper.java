package org.jboss.jbossset.bugclerk.checks.utils;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Collection;

import org.jboss.jbossset.bugclerk.Violation;

public final class AssertsHelper {

    private AssertsHelper() {}

    public static void assertResultsIsAsExpected(Collection<Violation> violations, String checkname, int bugId) {
        assertResultsIsAsExpected(violations,checkname,bugId,1);
    }

    public static void assertResultsIsAsExpected(Collection<Violation> violations, String checkname, int bugId, int nbViolationExpected) {
        assertThat(violations.size(), is(nbViolationExpected));
        for ( Violation v : violations ) {
            assertThat(v.getBug().getId(), is(bugId));
            assertThat(v.getCheckName(), is(checkname));
        }
    }
}
