/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2017, Red Hat, Inc., and individual contributors
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

import org.jboss.jbossset.bugclerk.AbstractCheckRunner;
import org.jboss.jbossset.bugclerk.Candidate;
import org.jboss.jbossset.bugclerk.MockUtils;
import org.jboss.jbossset.bugclerk.aphrodite.AphroditeClient;
import org.jboss.jbossset.bugclerk.checks.utils.CollectionUtils;
import org.jboss.set.aphrodite.domain.FlagStatus;
import org.jboss.set.aphrodite.domain.Stream;
import org.jboss.set.aphrodite.domain.StreamComponent;
import org.jboss.set.aphrodite.issue.trackers.jira.JiraIssue;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.jboss.jbossset.bugclerk.checks.utils.AssertsHelper.assertResultsIsAsExpected;

public class PRLinksNotValid extends AbstractCheckRunner {
    private String summary;
    private String product;
    private String targetRelease;
    private String check;

    @Before
    public void resetMockData() {
        summary = "A Summary...";
        targetRelease = "7.1.0.GA";
        check = checkName;
    }

    @Test
    public void testWrongHost() {
        product = LabelsHelper.JBEAPProject;
        String pullRequest = "https://badHost.com/aeshell/aesh.git/pull/88";
        mockIssueAndTest(pullRequest, 1);
    }

    private void mockIssueAndTest(String pullRequest, int nbViolationExpected) {
        String bugId = "4367";
        JiraIssue issue = MockUtils.mockJiraIssue(bugId, summary);
        Mockito.when(issue.getSprintRelease()).thenReturn("EAP 7.0.3");
        Mockito.when(issue.getProduct()).thenReturn(Optional.of(product));
        Map<String, FlagStatus> streamStatus = Collections.singletonMap(targetRelease, FlagStatus.ACCEPTED);
        Mockito.when(issue.getStreamStatus()).thenReturn(streamStatus);

        Mockito.when(issue.getPullRequests()).thenReturn(MockUtils.mockPullRequestsUrls(pullRequest));
        assertResultsIsAsExpected(engine.runCheckOnBugs(CollectionUtils.asSetOf(new Candidate(issue)), check),
                check, bugId, nbViolationExpected);
    }

    @Test
    public void testNotPRAndNotCommit() {
        product = "WildFly";
        String pullRequest = "https://github.com/aeshell/aesh/wrong/88";
        mockIssueAndTest(pullRequest, 1);

    }

    @Test
    public void testInvalidPR() {
        product = LabelsHelper.JBEAPProject;
        String pullRequest = "https://github.com/pull/00";
        mockIssueAndTest(pullRequest, 1);
    }

    @Test
    public void testValidPRFor7Z() {
        product = LabelsHelper.JBEAPProject;
        String pullRequest = "https://github.com/aeshell/aesh/pull/266";
        mockIssueAndTest(pullRequest, 0);
    }

    @Test
    public void testInValidPRFor70Z() {
        product = LabelsHelper.JBEAPProject;
        targetRelease = "7.0.z.GA";
        String pullRequest = "https://github.com/aeshell/aesh/pull/266";
        mockIssueAndTest(pullRequest, 1);
    }

    @Test
    public void testValidPRFor70Z() {
        product = LabelsHelper.JBEAPProject;
        targetRelease = "7.0.z.GA";
        String pullRequest = "https://github.com/jbossas/jboss-dmr/pull/266";
        mockIssueAndTest(pullRequest, 0);
    }

    @Test
    public void testValidCommit() {
        product = "WildFly";
        String pullRequest = "https://github.com/apache/commons-cli/commit/8204147a19e1b7c2e137e7e8ddcc66c74ec54088";
        mockIssueAndTest(pullRequest, 0);
    }

    @Test
    public void testValidCommitWFCORE() {
        product = "WildFly";
        String pullRequest = "https://github.com/apache/commons-cli/commit/8204147a19e1b7c2e137e7e8ddcc66c74ec54088";
        mockIssueAndTest(pullRequest, 0);
    }

    @Test
    public void testNoStreamsFound() {
        product = LabelsHelper.JBEAPProject;
        targetRelease = "WrongRelease";
        check = "PRLinksNoStreamsFound";
        String pullRequest = "https://github.com/apache/commons-cli/commit/8204147a19e1b7c2e137e7e8ddcc66c74ec54088";
        mockIssueAndTest(pullRequest, 1);
    }

    @Override
    protected AphroditeClient mockAphroditeClientIfNeeded() {
        AphroditeClient aphroditeClient = super.mockAphroditeClientIfNeeded();
        Mockito.when(aphroditeClient.getAllStreams()).thenReturn(createFakeStreams());
        return aphroditeClient;
    }

    private List<Stream> createFakeStreams() {
        List<Stream> allStreams = new ArrayList<>();
        allStreams.add(createStreamWithRepo("wildfly", "https://github.com/apache/commons-cli.git"));
        allStreams.add(createStreamWithRepo("jboss-eap-7.z.0", "https://github.com/aeshell/aesh.git"));
        allStreams.add(createStreamWithRepo("jboss-eap-7.0.z", "https://github.com/jbossas/jboss-dmr"));
        return allStreams;
    }

    private Stream createStreamWithRepo(String streamName, String repository) {
        Stream stream = new Stream(streamName);
        try {
            StreamComponent component = new StreamComponent("Component", null, null, new URI(repository),
                    null, null, null, null, null);
            stream.addComponent(component);

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return stream;
    }
}