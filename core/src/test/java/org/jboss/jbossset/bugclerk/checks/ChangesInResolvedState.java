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
import org.jboss.set.aphrodite.domain.IssueStatus;
import org.jboss.set.aphrodite.domain.User;
import org.jboss.set.aphrodite.issue.trackers.jira.JiraChangelogGroup;
import org.jboss.set.aphrodite.issue.trackers.jira.JiraChangelogItem;
import org.jboss.set.aphrodite.issue.trackers.jira.JiraIssue;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import static org.jboss.jbossset.bugclerk.checks.utils.AssertsHelper.assertResultsIsAsExpected;
import static org.junit.Assert.assertEquals;

public class ChangesInResolvedState extends AbstractCheckRunner {
    private static String RESOLVED_STATUS = "Resolved";
    
    private String bugId;
    private List<JiraChangelogGroup> changelog;
    private IssueStatus issueStatus;

    @Before
    public void resetMockData() {
        bugId = "143794";
        resetChangelog();
    }

    private void resetChangelog() {
        changelog = new ArrayList<>();
    }

    @Test
    public void testAddedPRAfterIssueInResolvedStatus() {
        prepareChangelogWithResolvedStatus();
        addPRToChangelog(new GregorianCalendar(2020, 1, 1).getTime());
        testJiraIssueViolatesRules(true);
    }

    @Test
    public void testAddedPRBeforeIssueInResolvedStatus() {
        prepareChangelogWithResolvedStatus();
        addPRToChangelog(new GregorianCalendar(1999, 1, 1).getTime());
        testJiraIssueViolatesRules(false);
    }

    @Test
    public void testAddedLinkAfterIssueInResolvedState() {
        prepareChangelogWithResolvedStatus();
        addLinkToChangelog(new GregorianCalendar(2020, 1, 1).getTime());
        testJiraIssueViolatesRules(true);
    }

    @Test
    public void testChangedStatusToOnQA() {
        prepareChangelogWithResolvedStatus();
        issueStatus = IssueStatus.ON_QA;
        addStatusChangeToOnQA(new GregorianCalendar(2020, 1, 1).getTime());
        testJiraIssueViolatesRules(false);
    }

    @Test
    public void testGetLastDateWhenResolved() {
        prepareChangelogWithResolvedStatus();
        addResolvedStateToChangelog(new GregorianCalendar(2000, 2, 1).getTime());
        addPRToChangelog(new GregorianCalendar(2000, 3, 1).getTime());

        Date lastDateResolved = new GregorianCalendar(2000, 4, 1).getTime();
        addResolvedStateToChangelog(lastDateResolved);
        assertEquals(JiraChangelogHelpers.getLastSetToStatusDate(changelog,RESOLVED_STATUS), lastDateResolved);
    }

    private void prepareChangelogWithResolvedStatus() {
        resetChangelog();
        issueStatus = IssueStatus.MODIFIED;
        addResolvedStateToChangelog(new GregorianCalendar(2000, 1, 1).getTime());
    }

    private void addResolvedStateToChangelog(Date dateResolved) {
        addGroupWithItem(new JiraChangelogItem("Status", "null", "null", "null", RESOLVED_STATUS), dateResolved);
    }

    private void addPRToChangelog(Date dateCreated) {
        addGroupWithItem(new JiraChangelogItem("Git pull request", "null", "null", "null",
                "https://github.com/jbossas/jboss-eap7/pull/578"), dateCreated);
    }

    private void addLinkToChangelog(Date dateCreated) {
        addGroupWithItem(new JiraChangelogItem("Link", "null", "null", "null",
                "This issue incorporates : link"), dateCreated);
    }

    private void addStatusChangeToOnQA(Date dateCreated) {
        addGroupWithItem(new JiraChangelogItem("Status", "null", RESOLVED_STATUS, "null",
                "Ready for QA"), dateCreated);
    }

    private void addGroupWithItem(JiraChangelogItem item, Date created) {
        List<JiraChangelogItem> items = new ArrayList<>();
        items.add(item);
        changelog.add(new JiraChangelogGroup(User.createWithUsername("mmarusic"), created, items));
    }

    private void testJiraIssueViolatesRules(boolean violationExpected) {
        int numberViolationExpected = violationExpected ? 1 : 0;
        JiraIssue jiraIssueMock = createJiraIssueMock();
        assertResultsIsAsExpected(engine.runCheckOnBugs(CollectionUtils.asSetOf(new Candidate(jiraIssueMock)), checkName),
                checkName, bugId, numberViolationExpected);
        assertEquals(JiraChangelogHelpers.isChangedAfterSetToStatus(changelog, RESOLVED_STATUS), violationExpected);
    }

    private JiraIssue createJiraIssueMock() {
        JiraIssue mock = (JiraIssue) MockUtils.mockJira(bugId, "A Summary...");
        Mockito.when(mock.getChangelog()).thenReturn(changelog);
        Mockito.when(mock.getStatus()).thenReturn(issueStatus);
        Mockito.when(mock.getSprintRelease()).thenReturn("EAP 7.0.3");
        return mock;
    }
}
