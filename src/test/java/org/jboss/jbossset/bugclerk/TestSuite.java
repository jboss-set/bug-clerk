package org.jboss.jbossset.bugclerk;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    org.jboss.jbossset.bugclerk.checks.ComponentUpgradeMissingFixList.class,
    org.jboss.jbossset.bugclerk.checks.PostMissingPR.class,
    org.jboss.jbossset.bugclerk.checks.ReleaseVersionMismatch.class,
    org.jboss.jbossset.bugclerk.checks.TargetRelease.class,
    org.jboss.jbossset.bugclerk.checks.IssueNotAssigned.class,

    org.jboss.jbossset.bugclerk.utils.CollectionsUtilsTests.class,
    org.jboss.jbossset.bugclerk.utils.URLUtilsTest.class,
    org.jboss.jbossset.bugclerk.utils.StringUtilsTests.class,
    org.jboss.jbossset.bugclerk.ReportEngineTest.class,
    org.jboss.jbossset.bugclerk.bugzilla.ReportViolationToBzEngineTest.class
})
public class TestSuite {

}
