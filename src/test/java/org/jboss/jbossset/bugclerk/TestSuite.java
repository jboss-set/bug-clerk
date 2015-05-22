package org.jboss.jbossset.bugclerk;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    org.jboss.jbossset.bugclerk.checks.FilterIssueEntries.class,
    org.jboss.jbossset.bugclerk.checks.ComponentUpgradeMissingFixList.class,
    org.jboss.jbossset.bugclerk.checks.PostMissingPR.class,
    org.jboss.jbossset.bugclerk.checks.ReleaseVersionMismatch.class,
    org.jboss.jbossset.bugclerk.checks.TargetRelease.class,
    org.jboss.jbossset.bugclerk.checks.IssueNotAssigned.class,
    org.jboss.jbossset.bugclerk.checks.BZShouldHaveDevAckFlag.class,
    org.jboss.jbossset.bugclerk.checks.BZShouldHaveQaAckFlag.class,
    org.jboss.jbossset.bugclerk.checks.CommunityBZ.class,
    org.jboss.jbossset.bugclerk.checks.OneOffPatchNotForSet.class,
    org.jboss.jbossset.bugclerk.checks.BZDepsShouldAlsoHaveFlags.class,

    org.jboss.jbossset.bugclerk.utils.CollectionsUtilsTest.class,
    org.jboss.jbossset.bugclerk.utils.URLUtilsTest.class,
    org.jboss.jbossset.bugclerk.utils.StringUtilsTest.class,
    org.jboss.jbossset.bugclerk.ReportEngineTest.class,
    org.jboss.jbossset.bugclerk.bugzilla.ReportViolationToBzEngineTest.class
})
public class TestSuite {

}
