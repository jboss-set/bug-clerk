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

package org.jboss.jbossset.bugclerk.utils;

import org.jboss.jbossset.bugclerk.MockUtils;
import org.jboss.jbossset.bugclerk.checks.utils.CollectionUtils;
import org.jboss.set.aphrodite.domain.Issue;
import org.jboss.set.aphrodite.domain.IssueType;
import org.jboss.set.aphrodite.domain.Release;
import org.jboss.set.aphrodite.issue.trackers.jira.JiraIssue;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

/**
 * Created by Marek Marusic <mmarusic@redhat.com> on 7/12/17.
 */
public class FixVersionHelperTest {
    public static final String correctVersion = "7.0.7.GA";
    public static final String wrongVersion = "7.0.8.GA";
    public static final String bugId = "5706";

    @Test
    public void isIssueUpgrade() throws Exception {
        JiraIssue upgradeIssue = createIssueWithType(IssueType.UPGRADE);

        assertEquals("This should be upgrade issue.", true, FixVersionHelper.isIssueUpgrade(upgradeIssue));

        JiraIssue bugIssue = createIssueWithType(IssueType.BUG);
        assertEquals("Bug issue should NOT be upgrade issue.", false, FixVersionHelper.isIssueUpgrade(bugIssue));

        JiraIssue issueWithNullType = createIssueWithType(null);
        assertEquals("This should NOT be upgrade issue.", false, FixVersionHelper.isIssueUpgrade(issueWithNullType));
    }

    public static JiraIssue createIssueWithType(IssueType issueType) {
        JiraIssue mock = createIssueMock();
        Mockito.when(mock.getType()).thenReturn(issueType);
        return mock;
    }

    public static JiraIssue createIssueMock() {
        JiraIssue mock = MockUtils.mockJiraIssue(bugId, "A Summary...");
        Mockito.when(mock.getSprintRelease()).thenReturn("EAP 7.0.3");
        return mock;
    }

    @Test
    public void filterIssuesWithDifferentFixVersion() throws Exception {
        testWithCorrectVersions();
        testWithWrongVersions();
    }

    private void testWithCorrectVersions() {
        List<Issue> issuesWithDifferentVersion = createIssuesAndFilterIssueWithWrongVersion(correctVersion, correctVersion);
        assertEquals("Check if there is no issue with wrong version found.", 0, issuesWithDifferentVersion.size());
    }

    private void testWithWrongVersions() {
        List<Issue> issuesWithDifferentVersion = createIssuesAndFilterIssueWithWrongVersion(correctVersion, wrongVersion);
        assertEquals("Check if there is 1 issue with wrong version.", 1, issuesWithDifferentVersion.size());
        Optional<String> versionOfFoundIssue = (issuesWithDifferentVersion.size() > 0)
                ? issuesWithDifferentVersion.get(0).getReleases().get(0).getVersion() : Optional.of("");
        assertEquals("Check if the bug issue with wrong 7.0.8.GA version is found.", versionOfFoundIssue,
                Optional.of(wrongVersion));
    }

    private List<Issue> createIssuesAndFilterIssueWithWrongVersion(String version, String version1) {
        JiraIssue upgradeIssue = createIssueWithRelease(new Release(version));
        JiraIssue bugIssue = createIssueWithRelease(new Release(version1));

        return FixVersionHelper.filterIssuesWithDifferentFixVersionOrSprint(upgradeIssue, CollectionUtils.asListOf(bugIssue));
    }

    private JiraIssue createIssueWithRelease(Release release) {
        JiraIssue mock = createIssueMock();
        Mockito.when(mock.getReleases()).thenReturn(CollectionUtils.asListOf(release));
        return mock;
    }
}