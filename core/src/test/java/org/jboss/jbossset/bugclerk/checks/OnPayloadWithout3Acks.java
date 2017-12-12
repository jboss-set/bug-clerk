package org.jboss.jbossset.bugclerk.checks;

import org.jboss.jbossset.bugclerk.AbstractCheckRunner;
import org.jboss.jbossset.bugclerk.Candidate;
import org.jboss.jbossset.bugclerk.MockUtils;
import org.jboss.jbossset.bugclerk.checks.utils.AssertsHelper;
import org.jboss.jbossset.bugclerk.checks.utils.CollectionUtils;
import org.jboss.set.aphrodite.domain.*;
import org.jboss.set.aphrodite.issue.trackers.jira.JiraIssue;
import org.junit.Test;
import org.mockito.Mockito;

import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.jboss.set.aphrodite.domain.FlagStatus.ACCEPTED;
import static org.jboss.set.aphrodite.domain.FlagStatus.SET;

public class OnPayloadWithout3Acks extends AbstractCheckRunner {

    private String bugId = "143794";

    @Test
    public void issuePendingDevAckShouldFail() {
        final JiraIssue mock = mock("7.0.9.GA", FlagStatus.SET, FlagStatus.ACCEPTED, FlagStatus.ACCEPTED);

        AssertsHelper.assertResultsIsAsExpected(engine.runCheckOnBugs(CollectionUtils.asSetOf(new Candidate(mock)), checkName), checkName, bugId);
    }

    @Test
    public void issueWithAllAcksShouldBeAccepted() {
        final JiraIssue mock = mock("7.0.9.GA", FlagStatus.ACCEPTED, FlagStatus.ACCEPTED, FlagStatus.ACCEPTED);

        AssertsHelper.assertResultsIsAsExpected(engine.runCheckOnBugs(CollectionUtils.asSetOf(new Candidate(mock)), checkName), checkName, bugId, 0);
    }

    @Test
    public void issueWithMissingAckFlagShouldFail() {
        final JiraIssue mock = mock("7.0.9.GA", FlagStatus.ACCEPTED, null, null);

        AssertsHelper.assertResultsIsAsExpected(engine.runCheckOnBugs(CollectionUtils.asSetOf(new Candidate(mock)), checkName), checkName, bugId);
    }

    @Test
    public void issueWithRejectedAckFlagShouldFail() {
        final JiraIssue mock = mock("7.1.1.GA", FlagStatus.ACCEPTED, FlagStatus.ACCEPTED, FlagStatus.REJECTED);

        AssertsHelper.assertResultsIsAsExpected(engine.runCheckOnBugs(CollectionUtils.asSetOf(new Candidate(mock)), checkName), checkName, bugId);
    }

    @Test
    public void issuePendingDevAckInZStreamShouldBeAccepted() {
        final JiraIssue mock = mock("7.0.z.GA", FlagStatus.SET, FlagStatus.ACCEPTED, FlagStatus.ACCEPTED);

        AssertsHelper.assertResultsIsAsExpected(engine.runCheckOnBugs(CollectionUtils.asSetOf(new Candidate(mock)), checkName), checkName, bugId, 0);
    }

    @Test
    public void issuePendingDevAckWithoutReleaseShouldFail() {
        final JiraIssue mock = mock(null, FlagStatus.SET, FlagStatus.ACCEPTED, FlagStatus.ACCEPTED);

        AssertsHelper.assertResultsIsAsExpected(engine.runCheckOnBugs(CollectionUtils.asSetOf(new Candidate(mock)), checkName), checkName, bugId, 0);
    }

    @Test
    public void bugzillaIssueWithAllAcksInPayloadShouldBeAccepted() {
        String payloadId = "143795";
        URL upgradeURL = MockUtils.buildURL(bugId);
        URL payloadTrackerUrl = MockUtils.buildURL(payloadId);

        // set up payload tracker
        Issue payloadTracker = MockUtils.mockBzIssue(payloadId, payloadTrackerUrl, "EAP 6.4.18 - Payload Tracker");
        Mockito.when(payloadTracker.getType()).thenReturn(IssueType.BUG);
        Mockito.when(payloadTracker.getDependsOn()).thenReturn(CollectionUtils.asListOf(upgradeURL));
        Mockito.when(mockAphroditeClientIfNeeded().retrieveIssue(Mockito.any())).thenReturn(Optional.of(payloadTracker));

        // set up tested bug
        final Issue bzIssue = MockUtils.mockBzIssue(bugId, upgradeURL, "summary");
        Mockito.when(bzIssue.getStatus()).thenReturn(IssueStatus.POST);
        Mockito.when(bzIssue.getBlocks()).thenReturn(Arrays.asList(payloadTrackerUrl));
        Stage stage = new Stage();
        stage.setStatus(Flag.DEV, ACCEPTED);
        stage.setStatus(Flag.QE, ACCEPTED);
        stage.setStatus(Flag.PM, ACCEPTED);
        Mockito.when(bzIssue.getStage()).thenReturn(stage);
        Mockito.when(bzIssue.getReleases()).thenReturn(Arrays.asList(new Release("EAP 6.4.18")));

        AssertsHelper.assertResultsIsAsExpected(
                engine.runChecksOnBugs(CollectionUtils.asSetOf(new Candidate(bzIssue), new Candidate(payloadTracker)),
                        Arrays.asList(checkName, INDEX_PAYLOAD_RULE, INDEX_ISSUE_RULE)), checkName, bugId, 0);
    }

    @Test
    public void bugzillaIssueWithMissingDevAckNotInPayloadShouldBeAccepted() {
        String payloadId = "143795";
        URL upgradeURL = MockUtils.buildURL(bugId);
        URL payloadTrackerUrl = MockUtils.buildURL(payloadId);

        // set up payload tracker
        Issue payloadTracker = MockUtils.mockBzIssue(payloadId, payloadTrackerUrl, "EAP 6.4.18 - Payload Tracker");
        Mockito.when(payloadTracker.getType()).thenReturn(IssueType.BUG);
        Mockito.when(payloadTracker.getDependsOn()).thenReturn(CollectionUtils.asListOf(upgradeURL));
        Mockito.when(mockAphroditeClientIfNeeded().retrieveIssue(Mockito.any())).thenReturn(Optional.of(payloadTracker));

        // set up tested bug
        final Issue bzIssue = MockUtils.mockBzIssue(bugId, upgradeURL, "summary");
        Mockito.when(bzIssue.getStatus()).thenReturn(IssueStatus.POST);
        Stage stage = new Stage();
        stage.setStatus(Flag.DEV, SET);
        stage.setStatus(Flag.QE, SET);
        stage.setStatus(Flag.PM, SET);
        Mockito.when(bzIssue.getStage()).thenReturn(stage);

        AssertsHelper.assertResultsIsAsExpected(
                engine.runChecksOnBugs(CollectionUtils.asSetOf(new Candidate(bzIssue), new Candidate(payloadTracker)),
                        Arrays.asList(checkName, INDEX_PAYLOAD_RULE, INDEX_ISSUE_RULE)), checkName, bugId, 0);
    }


    @Test
    public void bugzillaIssueWithMissingDevAckInPayloadShouldFail() {
        String payloadId = "143795";
        URL upgradeURL = MockUtils.buildURL(bugId);
        URL payloadTrackerUrl = MockUtils.buildURL(payloadId);

        // set up payload tracker
        Issue payloadTracker = MockUtils.mockBzIssue(payloadId, payloadTrackerUrl, "EAP 6.4.18 - Payload Tracker");
        Mockito.when(payloadTracker.getType()).thenReturn(IssueType.BUG);
        Mockito.when(payloadTracker.getDependsOn()).thenReturn(CollectionUtils.asListOf(upgradeURL));
        Mockito.when(mockAphroditeClientIfNeeded().retrieveIssue(Mockito.any())).thenReturn(Optional.of(payloadTracker));

        // set up tested bug
        final Issue bzIssue = MockUtils.mockBzIssue(bugId, upgradeURL, "summary");
        Mockito.when(bzIssue.getStatus()).thenReturn(IssueStatus.POST);
        Mockito.when(bzIssue.getBlocks()).thenReturn(Arrays.asList(payloadTrackerUrl));
        Stage stage = new Stage();
        stage.setStatus(Flag.DEV, SET);
        stage.setStatus(Flag.QE, ACCEPTED);
        stage.setStatus(Flag.PM, ACCEPTED);
        Mockito.when(bzIssue.getStage()).thenReturn(stage);
        Mockito.when(bzIssue.getReleases()).thenReturn(Arrays.asList(new Release("EAP 6.4.18")));

        AssertsHelper.assertResultsIsAsExpected(
                engine.runChecksOnBugs(CollectionUtils.asSetOf(new Candidate(bzIssue), new Candidate(payloadTracker)),
                        Arrays.asList(checkName, INDEX_PAYLOAD_RULE, INDEX_ISSUE_RULE)), checkName, bugId);
    }


    private JiraIssue mock(String release, FlagStatus dev, FlagStatus qe, FlagStatus pm) {
        final JiraIssue mock = MockUtils.mockJiraIssue(bugId, "summary");
        Mockito.when(mock.getSprintRelease()).thenReturn("");
        Mockito.when(mock.getStatus()).thenReturn(IssueStatus.POST);

        Stage stage = new Stage();
        if (dev != null) {
            stage.setStatus(Flag.DEV, dev);
        }
        if (qe != null) {
            stage.setStatus(Flag.QE, qe);
        }
        if (pm != null) {
            stage.setStatus(Flag.PM, pm);
        }
        Mockito.when(mock.getStage()).thenReturn(stage);

        if (release != null) {
            Mockito.when(mock.getReleases()).thenReturn(Arrays.asList(new Release(release)));
        }

        return mock;
    }
}
