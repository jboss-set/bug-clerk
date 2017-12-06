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
import org.jboss.jbossset.bugclerk.checks.utils.CollectionUtils;
import org.jboss.set.aphrodite.domain.FlagStatus;
import org.jboss.set.aphrodite.domain.Issue;
import org.jboss.set.aphrodite.issue.trackers.jira.JiraIssue;
import org.jboss.set.aphrodite.issue.trackers.jira.JiraLabel;
import org.junit.Test;
import org.mockito.Mockito;

import javax.naming.NameNotFoundException;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.jboss.jbossset.bugclerk.checks.utils.AssertsHelper.assertResultsIsAsExpected;
import static org.junit.Assert.assertEquals;

/**
 * Created by Marek Marusic <mmarusic@redhat.com> on 6/12/17.
 */
public class MissingDownstreamDependencyLabel extends AbstractCheckRunner {
    private JiraIssue issue;
    private String WFLYID = "WFLY-6673";
    private String JBEAPID = "EAP7-198";
    final String bugId = "1437945";
    private List<Issue> upstreamReferences = new ArrayList<>();

    @Test
    public void testIssueWithEmptyProductField() {
        JiraIssue issuWithEmptyProduct = createJiraIssueMock();
        Mockito.when(issuWithEmptyProduct.getProduct()).thenReturn(Optional.ofNullable(null));
        assertEquals(LabelsHelper.isIssueJBEAP(issuWithEmptyProduct), false);
    }

    private JiraIssue createJiraIssueMock() {
        upstreamReferences.clear();
        JiraIssue mock = MockUtils.mockJira(bugId, "A Summary...");
        Mockito.when(mock.getSprintRelease()).thenReturn("EAP 7.0.3");
        Mockito.when(mock.getProduct()).thenReturn(Optional.ofNullable(LabelsHelper.JBEAPProject));
        Map<String, FlagStatus> streamStatus = Collections.singletonMap("7.0.7.GA", FlagStatus.ACCEPTED);
        Mockito.when(mock.getStreamStatus()).thenReturn(streamStatus);

        List<URL> blocksIssues = new ArrayList<>();
        Mockito.when(mock.getDependsOn()).thenReturn(blocksIssues);

        try {
            Mockito.when(mock.getUpstreamReferences()).thenReturn(upstreamReferences.stream());
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return mock;
    }

    @Test
    public void testWfcoreIssueHasDownstreamDepLabel() {
        issue = createJiraIssueMock();
        upstreamReferences.add(createUpstreamIssueWithDependecnyLabel(WFLYID));

        assertResultsIsAsExpected(engine.runCheckOnBugs(CollectionUtils.asSetOf(new Candidate(issue)), checkName),
                checkName, bugId, 0);
    }

    private JiraIssue createUpstreamIssueWithDependecnyLabel(String issueId) {
        JiraIssue issue = createUpstreamIssueWithOutDependencyLabel(issueId);

        List<JiraLabel> labels = new ArrayList<>();
        labels.add(new JiraLabel(LabelsHelper.DOWNSTREAM_DEP));
        Mockito.when(issue.getLabels()).thenReturn(labels);
        return issue;
    }

    private JiraIssue createUpstreamIssueWithOutDependencyLabel(String issueId) {
        JiraIssue issue = MockUtils.mockJira(issueId, "A Summary...");
        Mockito.when(issue.getProduct()).thenReturn(Optional.ofNullable(LabelsHelper.JBEAPProject));
        Map<String, FlagStatus> streamStatus = Collections.singletonMap("7.1.0.GA", FlagStatus.ACCEPTED);
        Mockito.when(issue.getStreamStatus()).thenReturn(streamStatus);
        return issue;
    }

    @Test
    public void testWflyMissingDownstreamDepLabel() {
        issue = createJiraIssueMock();
        upstreamReferences.add(createUpstreamIssueWithOutDependencyLabel(WFLYID));

        assertResultsIsAsExpected(engine.runCheckOnBugs(CollectionUtils.asSetOf(new Candidate(issue)), checkName),
                checkName, bugId, 1);
    }

    @Test
    public void testJBEAPIssueHasDownstreamDepLabel() {
        issue = createJiraIssueMock();
        upstreamReferences.add(createUpstreamIssueWithDependecnyLabel(JBEAPID));

        assertResultsIsAsExpected(engine.runCheckOnBugs(CollectionUtils.asSetOf(new Candidate(issue)), checkName),
                checkName, bugId, 0);
    }

    @Test
    public void testJBEAPMissingDownstreamDepLabel() {
        issue = createJiraIssueMock();
        upstreamReferences.add(createUpstreamIssueWithOutDependencyLabel(JBEAPID));

        assertResultsIsAsExpected(engine.runCheckOnBugs(CollectionUtils.asSetOf(new Candidate(issue)), checkName),
                checkName, bugId, 1);
    }
}
