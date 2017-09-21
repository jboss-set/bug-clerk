package org.jboss.jbossset.bugclerk.checks;

import org.jboss.jbossset.bugclerk.AbstractCheckRunner;
import org.jboss.jbossset.bugclerk.Candidate;
import org.jboss.jbossset.bugclerk.MockUtils;
import org.jboss.jbossset.bugclerk.checks.utils.AssertsHelper;
import org.jboss.jbossset.bugclerk.checks.utils.CollectionUtils;
import org.jboss.set.aphrodite.domain.*;
import org.junit.Test;
import org.mockito.Mockito;

public class IssueWorkedOnButNotQAAcked extends AbstractCheckRunner {

    @Test
    public void issueInAssignedStatePendingQAAckShouldFail() {
        String bugId = "143794";
        final Issue mock = MockUtils.mockBug(bugId, "summary");
        Mockito.when(mock.getStatus()).thenReturn(IssueStatus.ASSIGNED);
        Stage stage = new Stage();
        stage.setStatus(Flag.DEV, FlagStatus.ACCEPTED);
        stage.setStatus(Flag.QE, FlagStatus.SET);
        stage.setStatus(Flag.PM, FlagStatus.ACCEPTED);
        Mockito.when(mock.getStage()).thenReturn(stage);

        AssertsHelper.assertResultsIsAsExpected(engine.runCheckOnBugs(CollectionUtils.asSetOf(new Candidate(mock)), checkName), checkName, bugId);
    }

    @Test
    public void issueInAssignedStateWithRejectedQAAckShouldFail() {
        String bugId = "143794";
        final Issue mock = MockUtils.mockBug(bugId, "summary");
        Mockito.when(mock.getStatus()).thenReturn(IssueStatus.ASSIGNED);
        Stage stage = new Stage();
        stage.setStatus(Flag.DEV, FlagStatus.ACCEPTED);
        stage.setStatus(Flag.QE, FlagStatus.REJECTED);
        stage.setStatus(Flag.PM, FlagStatus.ACCEPTED);
        Mockito.when(mock.getStage()).thenReturn(stage);

        AssertsHelper.assertResultsIsAsExpected(engine.runCheckOnBugs(CollectionUtils.asSetOf(new Candidate(mock)), checkName), checkName, bugId);
    }

    @Test
    public void issueInAssignedStateWithApprovedQAAckShouldPass() {
        String bugId = "143794";
        final Issue mock = MockUtils.mockBug(bugId, "summary");
        Mockito.when(mock.getStatus()).thenReturn(IssueStatus.ASSIGNED);
        Stage stage = new Stage();
        stage.setStatus(Flag.DEV, FlagStatus.ACCEPTED);
        stage.setStatus(Flag.QE, FlagStatus.ACCEPTED);
        stage.setStatus(Flag.PM, FlagStatus.ACCEPTED);
        Mockito.when(mock.getStage()).thenReturn(stage);

        AssertsHelper.assertResultsIsAsExpected(engine.runCheckOnBugs(CollectionUtils.asSetOf(new Candidate(mock)), checkName), checkName, bugId, 0);
    }
}