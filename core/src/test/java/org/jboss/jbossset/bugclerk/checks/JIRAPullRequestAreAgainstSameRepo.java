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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.jboss.jbossset.bugclerk.AbstractCheckRunner;
import org.jboss.jbossset.bugclerk.Candidate;
import org.jboss.jbossset.bugclerk.MockUtils;
import org.jboss.jbossset.bugclerk.RulesEngine;
import org.jboss.jbossset.bugclerk.aphrodite.AphroditeClient;
import org.jboss.jbossset.bugclerk.checks.utils.CollectionUtils;
import org.jboss.set.aphrodite.domain.Codebase;
import org.jboss.set.aphrodite.domain.PullRequest;
import org.jboss.set.aphrodite.domain.PullRequestState;
import org.jboss.set.aphrodite.domain.Repository;
import org.jboss.set.aphrodite.issue.trackers.jira.JiraIssue;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class JIRAPullRequestAreAgainstSameRepo extends AbstractCheckRunner {
    private static final String RULE = "JIRAPullRequestAreAgainstSameRepo";
    private static final String MOCK_PULL_REQUEST_ID_1 = "https://github.com/aeshell/aesh.git/pull/88";
    private static final String MOCK_PULL_REQUEST_ID_2 = "https://github.com/aeshell/aesh.git/pull/89";
    private static final String MOCK_BAD_PULL_REQUEST_ID_1 = "https://github.com/aeshell/bush_junior.git/pull/88";
    private static final String MOCK_BAD_PULL_REQUEST_ID_2 = "https://github.com/desktop/aesh.git/pull/89";
    private static final String MOCK_BAD__CODEBASE_PULL_REQUEST_ID_2 = "https://github.com/aeshell/aesh.git/pull/90";

    private JiraIssue issueWithGoodPRs;
    private JiraIssue issueWithPRToWrongRepo_1;
    private JiraIssue issueWithPRToWrongRepo_2;
    private JiraIssue issueWithPRToWrongCodeBase;


    @Before
    public void init() throws MalformedURLException {
        AphroditeClient client = Mockito.mock(AphroditeClient.class);
        Mockito.when(client.getAllStreams()).thenReturn(MockUtils.mockStreamsWithStreamWithOneComponent("jboss-eap-7.0.z", "aesh","https://github.com/aeshell/aesh.git", "0.66.+","0.66.12", "0.66.12.redhat-1", "org.jboss.aesh"));
        Mockito.when(client.getPullRequestAsString(anyString())).thenAnswer(new Answer<PullRequest>() {

            @Override
            public PullRequest answer(InvocationOnMock invocation) throws Throwable {
                return mockPullRequest((String)invocation.getArguments()[0]);
            }
        });
        this.engine = new RulesEngine(new HashMap<String, Object>(0), client);

        this.issueWithGoodPRs = mockJira("issueWithGoodPRs", MOCK_PULL_REQUEST_ID_1, MOCK_PULL_REQUEST_ID_2);
        this.issueWithPRToWrongRepo_1 = mockJira("issueWithPRToWrongRepo_1", MOCK_PULL_REQUEST_ID_1, MOCK_BAD_PULL_REQUEST_ID_1);
        this.issueWithPRToWrongRepo_2 = mockJira("issueWithPRToWrongRepo_2", MOCK_PULL_REQUEST_ID_1, MOCK_BAD_PULL_REQUEST_ID_2);
        this.issueWithPRToWrongCodeBase = mockJira("issueWithPRToWrongCodeBase", MOCK_PULL_REQUEST_ID_1, MOCK_BAD__CODEBASE_PULL_REQUEST_ID_2);
    }

     private JiraIssue mockJira(final String id, final String pr1URL, final String pr2URL) throws MalformedURLException {
        JiraIssue jiraIssue =  MockUtils.mockJiraIssue(id, id);
        ArrayList<URL> prURLsList = new ArrayList<>();
        prURLsList.add(new URL(pr1URL));
        prURLsList.add(new URL(pr2URL));
        Mockito.when(jiraIssue.getPullRequests()).thenReturn(prURLsList);
        return jiraIssue;
     }

    private PullRequest mockPullRequest(final String url) {
        Codebase codebase = null;
        if (url.equals(MOCK_BAD__CODEBASE_PULL_REQUEST_ID_2)) {
            codebase = new Codebase("0.78");
        } else {
            codebase = new Codebase("0.7");
        }
        PullRequest mock = Mockito.mock(PullRequest.class);
        Mockito.when(mock.getCodebase()).thenReturn(codebase);
        try {
            Mockito.when(mock.getURL()).thenReturn(new URL(url));
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Repository repositoryMock = Mockito.mock(Repository.class);
        Mockito.when(repositoryMock.getURL()).thenReturn(stripPRToRepositoryURL(url));
        Mockito.when(mock.getRepository()).thenReturn(repositoryMock);
        return mock;
    }

    private URL stripPRToRepositoryURL(final String prURL) {
        try {
            return new URL(prURL.replaceFirst("/pull/[0-9]*", ""));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testProperLinks() {
        assertResultsIsAsExpected(
                engine.runChecksOnBugs(CollectionUtils.asSetOf(new Candidate(issueWithGoodPRs)),
                        CollectionUtils.asListOf(RULE)),
                checkName,"issueWithGoodPRs", 0);
    }

    @Test
    public void testBadLinks() {
        assertResultsIsAsExpected(
                engine.runChecksOnBugs(CollectionUtils.asSetOf(new Candidate(issueWithPRToWrongRepo_1)),
                        CollectionUtils.asListOf(RULE)),
                checkName,"issueWithPRToWrongRepo_1", 1);
    }

    @Test
    public void testBadLinks2() {
        assertResultsIsAsExpected(
                engine.runChecksOnBugs(CollectionUtils.asSetOf(new Candidate(issueWithPRToWrongRepo_2)),
                        CollectionUtils.asListOf(RULE)),
                checkName,"issueWithPRToWrongRepo_2", 1);
    }

    @Test
    public void testBadLinks3() {
        assertResultsIsAsExpected(
                engine.runChecksOnBugs(CollectionUtils.asSetOf(new Candidate(issueWithPRToWrongCodeBase)),
                        CollectionUtils.asListOf(RULE)),
                checkName,"issueWithPRToWrongCodeBase", 1);
    }
}
