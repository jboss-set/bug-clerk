package org.jboss.jbossset.bugclerk.checks;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;

import org.jboss.jbossset.bugclerk.AbstractCheckRunner;
import org.jboss.jbossset.bugclerk.Candidate;
import org.jboss.jbossset.bugclerk.MockUtils;
import org.jboss.jbossset.bugclerk.aphrodite.AphroditeClient;
import org.jboss.set.aphrodite.domain.IssueStatus;
import org.jboss.set.aphrodite.domain.PullRequest;
import org.jboss.set.aphrodite.domain.PullRequestState;
import org.jboss.set.aphrodite.issue.trackers.jira.JiraIssue;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * @author Tomas Hofman (thofman@redhat.com)
 */
public class IssueStateModifiedAfterPrMerged extends AbstractCheckRunner {

    private String trackerId = "testid";
    private AphroditeClient aphroditeClient;

    @Override
    protected AphroditeClient mockAphroditeClientIfNeeded() {
        aphroditeClient = super.mockAphroditeClientIfNeeded();
        return aphroditeClient;
    }

    @Test
    public void issueModifiedPrClosed() throws MalformedURLException {
        testRule(IssueStatus.MODIFIED, PullRequestState.CLOSED, 0);
    }

    @Test
    public void issueModifiedPrOpen() throws MalformedURLException {
        testRule(IssueStatus.MODIFIED, PullRequestState.OPEN, 0);
    }

    @Test
    public void issuePostPrOpen() throws MalformedURLException {
        testRule(IssueStatus.POST, PullRequestState.OPEN, 0);
    }

    @Test
    public void issuePostPrClosed() throws MalformedURLException {
        testRule(IssueStatus.POST, PullRequestState.CLOSED, 1);
    }

    @Test
    public void issuePostNoPr() throws MalformedURLException {
        testRule(IssueStatus.POST, null, 0);
    }

    @Test
    public void issueModifiedNoPr() throws MalformedURLException {
        testRule(IssueStatus.MODIFIED, null, 0);
    }

    private void testRule(IssueStatus issueStatus, PullRequestState pullRequestState, int numberOfViolations)
            throws MalformedURLException {
        JiraIssue issue = MockUtils.mockJiraIssue(trackerId, "Test issue");
        Mockito.when(issue.getSprintRelease()).thenReturn("EAP 7.0.3");
        Mockito.when(issue.getStatus()).thenReturn(issueStatus);

        if (pullRequestState != null) {
            // mock aphrodite returning single PR with given state
            PullRequest pullRequest = new PullRequest("test", null, null, null, pullRequestState, null, null, false, false, null,null);
            Mockito.when(aphroditeClient.getPullRequests(Mockito.anyListOf(URL.class)))
                    .thenReturn(Collections.singletonList(pullRequest));
            Mockito.when(issue.getPullRequests()).thenReturn(Collections.singletonList(new URL("http://test.com")));
        } else {
            // mock aphrodite returning empty list of PRs
            Mockito.when(aphroditeClient.getPullRequests(Mockito.anyListOf(URL.class)))
                    .thenReturn(Collections.emptyList());
            Mockito.when(issue.getPullRequests()).thenReturn(Collections.emptyList());
        }

        Candidate candidate = new Candidate(issue);
        engine.runCheckOnBugs(Collections.singletonList(candidate), checkName);
        if (candidate.getViolations().size() != numberOfViolations) {
            Assert.fail("Number of violations doesn't match expected. Violations: "
                    + Arrays.toString(candidate.getViolations().toArray()));
        }
    }

}
