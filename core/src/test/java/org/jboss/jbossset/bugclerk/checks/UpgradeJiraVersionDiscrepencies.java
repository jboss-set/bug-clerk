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
import org.jboss.jbossset.bugclerk.aphrodite.AphroditeClient;
import org.jboss.jbossset.bugclerk.checks.utils.CollectionUtils;
import org.jboss.jbossset.bugclerk.utils.FixVersionHelperTest;
import org.jboss.set.aphrodite.domain.Issue;
import org.jboss.set.aphrodite.domain.IssueType;
import org.jboss.set.aphrodite.domain.Release;
import org.jboss.set.aphrodite.issue.trackers.jira.JiraIssue;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.jboss.jbossset.bugclerk.checks.utils.AssertsHelper.assertResultsIsAsExpected;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;

/**
 * Created by Marek Marusic <mmarusic@redhat.com> on 7/11/17.
 */
public class UpgradeJiraVersionDiscrepencies extends AbstractCheckRunner {
    private List<Issue> incorporatesIssuesOfMockedUpgradeIssue = new ArrayList<>();
    private AphroditeClient aphroditeClient;

    @Test
    public void testWrongtFixVersion() {
        JiraIssue upgradeIssue = createUpgradeIssue();
        incorporatesIssuesOfMockedUpgradeIssue.add(createIncorporatesIssueWithWrongFixVersion());
        checkViolations(upgradeIssue, 1);
    }

    private JiraIssue createUpgradeIssue() {
        incorporatesIssuesOfMockedUpgradeIssue.clear();
        JiraIssue upgradeIssue = FixVersionHelperTest.createIssueWithType(IssueType.UPGRADE);
        Mockito.when(upgradeIssue.getReleases())
                .thenReturn(CollectionUtils.asListOf(new Release(FixVersionHelperTest.correctVersion)));
        Mockito.when(upgradeIssue.getSprintRelease()).thenReturn(FixVersionHelperTest.correctVersion);

        return upgradeIssue;
    }

    private JiraIssue createIncorporatesIssueWithWrongFixVersion() {
        return createBugIssueWithRelease(new Release(FixVersionHelperTest.wrongVersion));
    }

    private JiraIssue createBugIssueWithRelease(Release release) {
        JiraIssue mock = FixVersionHelperTest.createIssueWithType(IssueType.BUG);
        Mockito.when(mock.getReleases()).thenReturn(CollectionUtils.asListOf(release));
        return mock;
    }

    private void checkViolations(JiraIssue upgradeIssue, int countOfIssuesWithWringFixVersion) {
        List<Issue> issuesWithWrongVersion = FixVersionHelper.getIncorporatedIssuesWithWrongFixVersionOrSprint(upgradeIssue,
                aphroditeClient);
        assertEquals("There should be " + countOfIssuesWithWringFixVersion + " issue with wrong version",
                countOfIssuesWithWringFixVersion,
                issuesWithWrongVersion.size());

        int numberOfViolations = (countOfIssuesWithWringFixVersion > 0) ? 1 : 0;
        assertResultsIsAsExpected(engine.runCheckOnBugs(CollectionUtils.asSetOf(new Candidate(upgradeIssue)), checkName),
                checkName, FixVersionHelperTest.bugId, numberOfViolations);
    }

    private Issue createIncorporatedIssueWithCorrectFixVersion() {
        return createBugIssueWithRelease(new Release(FixVersionHelperTest.correctVersion));
    }

    @Test
    public void testWrongSprintVersion() {
        JiraIssue upgradeIssue = createUpgradeIssue();
        addIncorporatesIssueWithCorrectVersionAndSprint(FixVersionHelperTest.wrongVersion);

        checkViolations(upgradeIssue, 1);
    }

    private void addIncorporatesIssueWithCorrectVersionAndSprint(String sprint) {
        JiraIssue issue = (JiraIssue) createIncorporatedIssueWithCorrectFixVersion();
        Mockito.when(issue.getSprintRelease()).thenReturn(sprint);
        incorporatesIssuesOfMockedUpgradeIssue.add(issue);
    }

    @Test
    public void testCorrectVersions() {
        JiraIssue upgradeIssue = createUpgradeIssue();
        addIncorporatesIssueWithCorrectVersionAndSprint(FixVersionHelperTest.correctVersion);

        checkViolations(upgradeIssue, 0);
    }

    @Override
    protected AphroditeClient mockAphroditeClientIfNeeded() {
        aphroditeClient = super.mockAphroditeClientIfNeeded();
        Mockito.when(aphroditeClient.loadIssuesFromUrls(any())).thenReturn(incorporatesIssuesOfMockedUpgradeIssue);
        return aphroditeClient;
    }
}
