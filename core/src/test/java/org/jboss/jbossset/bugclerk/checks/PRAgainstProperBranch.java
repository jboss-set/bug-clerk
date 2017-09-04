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
import static org.mockito.Matchers.anyString;

import java.util.HashMap;

import org.jboss.jbossset.bugclerk.AbstractCheckRunner;
import org.jboss.jbossset.bugclerk.Candidate;
import org.jboss.jbossset.bugclerk.MockUtils;
import org.jboss.jbossset.bugclerk.RulesEngine;
import org.jboss.jbossset.bugclerk.aphrodite.AphroditeClient;
import org.jboss.jbossset.bugclerk.checks.utils.CollectionUtils;
import org.jboss.set.aphrodite.domain.Codebase;
import org.jboss.set.aphrodite.domain.PullRequest;
import org.jboss.set.aphrodite.issue.trackers.jira.JiraIssue;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class PRAgainstProperBranch extends AbstractCheckRunner {

    private String summary;
    private String bugId;

    private static final String MOCK_PULL_REQUEST_ID = "https://github.com/aeshell/aesh.git/pull/88";

    @Before
    public void resetMockData() {
        bugId = "JBEAP-4367";
        summary = "A Summary...";
    }

    @Before
    public void initRuleEngine() {
        AphroditeClient client = Mockito.mock(AphroditeClient.class);
        Mockito.when(client.getAllStreams()).thenReturn(MockUtils.mockStreamsWithStreamWithOneComponent("jboss-eap-7.0.z", "aesh","https://github.com/aeshell/aesh.git", "0.66.+","0.66.12", "0.66.12.redhat-1", "org.jboss.aesh"));
        Mockito.when(client.getPullRequest(anyString())).thenAnswer(new Answer<PullRequest>() {

            @Override
            public PullRequest answer(InvocationOnMock invocation) throws Throwable {
                return mockPullRequest();
            }
        });
        this.engine = new RulesEngine(new HashMap<String, Object>(0),client);
    }

    private PullRequest mockPullRequest() {
        Codebase codebase = new Codebase("0.7");
        PullRequest mock = Mockito.mock(PullRequest.class);
        Mockito.when(mock.getCodebase()).thenReturn(codebase);
        return mock;
    }

    @Test
    public void noViolationIfPrAgainstProjectNotInCodeBases() { // because streams may not up to date
        JiraIssue mock = (JiraIssue) MockUtils.mockJiraIssue(bugId, summary);
        Mockito.when(mock.getSprintRelease()).thenReturn("EAP 7.0.3");
        Mockito.when(mock.getReleases()).thenReturn(MockUtils.mockReleases("7.0.z"));
        Mockito.when(mock.getPullRequests()).thenReturn(MockUtils.mockPullRequestsUrls("https://github.com/jbossas/jboss-eap7/pull/1259"));
        assertResultsIsAsExpected(engine.runCheckOnBugs(CollectionUtils.asSetOf(new Candidate(mock)), checkName), checkName, bugId, 0);
    }

    @Test
    public void violationIfPrAgainstProjectInCodeBasesButWrongBranch() {
        JiraIssue mock = (JiraIssue) MockUtils.mockJiraIssue(bugId, summary);
        Mockito.when(mock.getSprintRelease()).thenReturn("EAP 7.0.3");
        Mockito.when(mock.getReleases()).thenReturn(MockUtils.mockReleases("7.0.z"));
        Mockito.when(mock.getPullRequests()).thenReturn(MockUtils.mockPullRequestsUrls(MOCK_PULL_REQUEST_ID));
        assertResultsIsAsExpected(engine.runCheckOnBugs(CollectionUtils.asSetOf(new Candidate(mock)), checkName), checkName, bugId, 1);
    }
}
