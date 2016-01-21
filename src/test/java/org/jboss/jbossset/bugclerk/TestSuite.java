package org.jboss.jbossset.bugclerk;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ org.jboss.jbossset.bugclerk.checks.AssignedButStillOnSET.class,
        org.jboss.jbossset.bugclerk.checks.BZDepsShouldAlsoHaveFlags.class,
        org.jboss.jbossset.bugclerk.checks.BZShouldHaveDevAckFlag.class,
        org.jboss.jbossset.bugclerk.checks.BZShouldHaveQaAckFlag.class,
        org.jboss.jbossset.bugclerk.checks.BZShouldHaveTimeEstimate.class,
        org.jboss.jbossset.bugclerk.checks.CommunityBZ.class,
        org.jboss.jbossset.bugclerk.checks.ComponentUpgradeMissingFixList.class,
        org.jboss.jbossset.bugclerk.checks.FilterIssueEntries.class, org.jboss.jbossset.bugclerk.checks.IssueNotAssigned.class,
        org.jboss.jbossset.bugclerk.checks.OneOffPatchNotForSet.class,
        org.jboss.jbossset.bugclerk.checks.PostMissingPmAck.class, org.jboss.jbossset.bugclerk.checks.PostMissingPR.class,
        org.jboss.jbossset.bugclerk.checks.RegressionMayImpactOneOffRelease.class,
        org.jboss.jbossset.bugclerk.checks.ReleaseVersionMismatch.class,
        org.jboss.jbossset.bugclerk.checks.SummaryContainsPatchButTypeIsNotSupportPatch.class,
        org.jboss.jbossset.bugclerk.checks.TargetRelease.class, org.jboss.jbossset.bugclerk.ReportEngineTest.class,
        org.jboss.jbossset.bugclerk.BuildReportToUpdateTracker.class,
        org.jboss.jbossset.bugclerk.comments.CommentPatternMatcherTest.class,
        org.jboss.jbossset.bugclerk.utils.URLUtilsTest.class, })
public class TestSuite {

}
