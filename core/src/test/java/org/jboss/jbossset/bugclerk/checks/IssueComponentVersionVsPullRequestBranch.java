/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2015, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.jbossset.bugclerk.checks;

import static org.jboss.jbossset.bugclerk.checks.utils.AssertsHelper.assertResultsIsAsExpected;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.jboss.jbossset.bugclerk.AbstractCheckRunner;
import org.jboss.jbossset.bugclerk.Candidate;
import org.jboss.jbossset.bugclerk.MockUtils;
import org.jboss.jbossset.bugclerk.checks.utils.CollectionUtils;
import org.jboss.set.aphrodite.domain.PullRequest;
import org.jboss.set.aphrodite.domain.PullRequestState;
import org.jboss.set.aphrodite.domain.Release;
import org.jboss.set.aphrodite.domain.Stream;
import org.jboss.set.aphrodite.issue.trackers.jira.JiraIssue;
import org.junit.Test;
import org.mockito.Mockito;
public class IssueComponentVersionVsPullRequestBranch extends AbstractCheckRunner {

    private static final String RULE = "IssueComponentVersionVsPullRequestBranch";
    private static final String SPRINT_RELEASE = "EAP 7.0.2";
    //no idea why this trigger SprintVersionMismatch, require ^^
    @Test
    public void testNoEmptyPositive() {
        // general check, other tests have it, but lets be thorough
        final String issueId = "JBEAP-143816514096871666";

        JiraIssue mock = MockUtils.mockJiraIssue(issueId, "JIRA without a fancy title");
        Mockito.when(mock.getSprintRelease()).thenReturn(SPRINT_RELEASE);
        assertResultsIsAsExpected(
                engine.runChecksOnBugs(CollectionUtils.asSetOf(new Candidate(mock)),
                        CollectionUtils.asListOf(RULE)),
                checkName, issueId, 0);
    }

    @Test
    public void testNoViolation() throws MalformedURLException, URISyntaxException {
        final String issueId = "JBEAP-143816514096871667";
        final JiraIssue jiraIssue = MockUtils.mockJiraIssue(issueId, "JIRA without a fancy title");
        final List<Release> releases = new ArrayList<>();
        releases.add(new Release("7.0.z", "GA"));
        releases.add(new Release("7.1.z", "CR"));
        Mockito.when(jiraIssue.getReleases()).thenReturn(releases);
        final PullRequest goodPullRequest = MockUtils.mockPullRequest("https://gitdoll.org/comsat/doll/pull/chizle-13","Good PR","7.0.x", PullRequestState.OPEN);
        MockUtils.mockRepository(goodPullRequest,"https://gitdoll.org/comsat/doll",null);
        final PullRequest nonMatchingPR = MockUtils.mockPullRequest("https://gitdoll.org/comsat/prack/pull/chizle-14","Bad PR","7.x.0", PullRequestState.OPEN);
        MockUtils.mockRepository(nonMatchingPR,"https://gitdoll.org/comsat/prack",null);
        MockUtils.mockPullRequestReturn(super.mockAphroditeClientIfNeeded(), goodPullRequest,nonMatchingPR);
        final List<URL> mockedPRs = new ArrayList<>();
        mockedPRs.add(goodPullRequest.getURL());
        mockedPRs.add(nonMatchingPR.getURL());
        Mockito.when(jiraIssue.getPullRequests()).thenReturn(mockedPRs);

        final Stream mockNonMatchingStream = MockUtils.mockStream("jboss-eap-6.4.z", new String[] {"comp1","comp2"}, new String[] {"1.x","master"}, new String[] {"https://gitdoll.org/comsat/doll","https://gitdoll.org/chinchilla/doll"});
        final Stream mockMatchingStream = MockUtils.mockStream("jboss-eap-7.0.z", new String[] {"comp1","comp2"}, new String[] {"7.0.x","master"}, new String[] {"https://gitdoll.org/comsat/doll","https://gitdoll.org/doll/check"});
        MockUtils.mockStreamReturn(super.mockAphroditeClientIfNeeded(),mockNonMatchingStream, mockMatchingStream);

        Mockito.when(jiraIssue.getSprintRelease()).thenReturn("NO SPRINT");
        assertResultsIsAsExpected(
                engine.runChecksOnBugs(CollectionUtils.asSetOf(new Candidate(jiraIssue)),
                        CollectionUtils.asListOf(RULE)),
                checkName, issueId, 0);
    }
    
    @Test
    public void testViolation() throws MalformedURLException, URISyntaxException {
        final String issueId = "JBEAP-143816514096871667";
        final JiraIssue jiraIssue = MockUtils.mockJiraIssue(issueId, "JIRA without a fancy title");
        final List<Release> releases = new ArrayList<>();
        releases.add(new Release("7.0.z", "GA"));
        releases.add(new Release("7.1.z", "CR"));
        Mockito.when(jiraIssue.getReleases()).thenReturn(releases);
        final PullRequest goodPullRequest = MockUtils.mockPullRequest("https://gitdoll.org/comsat/doll/pull/chizle-13","Good PR","7.1.x", PullRequestState.OPEN);
        MockUtils.mockRepository(goodPullRequest,"https://gitdoll.org/comsat/doll",null);
        final PullRequest badPullRequest = MockUtils.mockPullRequest("https://gitdoll.org/comsat/doll/pull/chizle-14","Bad PR","7.x.0", PullRequestState.OPEN);
        MockUtils.mockRepository(badPullRequest,"https://gitdoll.org/comsat/doll",null);

        MockUtils.mockPullRequestReturn(super.mockAphroditeClientIfNeeded(), goodPullRequest,badPullRequest);
        final List<URL> mockedPRs = new ArrayList<>();
        mockedPRs.add(goodPullRequest.getURL());
        mockedPRs.add(badPullRequest.getURL());
        Mockito.when(jiraIssue.getPullRequests()).thenReturn(mockedPRs);

        final Stream mockNonMatchingStream = MockUtils.mockStream("jboss-eap-6.4.z", new String[] {"comp1","comp2"}, new String[] {"1.x","master"}, new String[] {"https://gitdoll.org/comsat/doll","https://gitdoll.org/chinchilla/doll"});
        final Stream mockMatchingStream = MockUtils.mockStream("jboss-eap-7.0.z", new String[] {"comp1","comp2"}, new String[] {"7.1.x","master"}, new String[] {"https://gitdoll.org/comsat/doll","https://gitdoll.org/doll/check"});
        MockUtils.mockStreamReturn(super.mockAphroditeClientIfNeeded(),mockNonMatchingStream, mockMatchingStream);

        Mockito.when(jiraIssue.getSprintRelease()).thenReturn("NO SPRINT");
        assertResultsIsAsExpected(
                engine.runChecksOnBugs(CollectionUtils.asSetOf(new Candidate(jiraIssue)),
                        CollectionUtils.asListOf(RULE)),
                checkName, issueId, 1);
    }

}