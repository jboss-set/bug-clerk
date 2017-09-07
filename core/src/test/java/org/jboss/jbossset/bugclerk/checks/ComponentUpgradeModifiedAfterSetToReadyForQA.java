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

import static org.jboss.jbossset.bugclerk.checks.utils.AssertsHelper.assertResultsIsAsExpected;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.jboss.jbossset.bugclerk.AbstractCheckRunner;
import org.jboss.jbossset.bugclerk.Candidate;
import org.jboss.jbossset.bugclerk.MockUtils;
import org.jboss.jbossset.bugclerk.checks.utils.CollectionUtils;
import org.jboss.set.aphrodite.domain.IssueStatus;
import org.jboss.set.aphrodite.domain.IssueType;
import org.jboss.set.aphrodite.domain.User;
import org.jboss.set.aphrodite.issue.trackers.jira.JiraChangelogGroup;
import org.jboss.set.aphrodite.issue.trackers.jira.JiraChangelogItem;
import org.jboss.set.aphrodite.issue.trackers.jira.JiraIssue;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class ComponentUpgradeModifiedAfterSetToReadyForQA extends AbstractCheckRunner {
    private static String READY_FOR_QA_STATUS = "qa in progress";
    
    private String bugId;
    private List<JiraChangelogGroup> changelog;
    private IssueStatus issueStatus;

    private int dateCursor;
    
    @Before
    public void resetMockData() {
        bugId = "143794";
        resetChangelog();
    }

    public void resetDateIncrementor() {
        dateCursor = 2000;
    }
    
    private void resetChangelog() {
        changelog = new ArrayList<>();
    }

    @Test
    public void noViolationIfNotAComponentUpgrade() {
        prepareChangelogWithStatus();
        testJiraIssueViolatesRules(false, createJiraIssueMock(IssueType.BUG));
    }
    
    @Test
    public void violationIfComponentUpgradeWithAlterChangelogHistory() {
        prepareChangelogWithStatus();
        addStatusChangeToChangelog(getNextDate(), "newStatus");
        testJiraIssueViolatesRules(true);
    }
    
    private Date getNextDate() {
        return new GregorianCalendar(dateCursor++, 1, 1).getTime();
    }
    
    private void prepareChangelogWithStatus() {
        prepareChangelogWithStatus(IssueStatus.ON_QA);
    }
    
    private void prepareChangelogWithStatus(IssueStatus status) {
        issueStatus = status;
        addStatusChangeToChangelog(getNextDate());
    }

    private void addStatusChangeToChangelog(Date dateResolved) {
        addStatusChangeToChangelog(dateResolved, READY_FOR_QA_STATUS);
    }
    
    private void addStatusChangeToChangelog(Date changeDate, String status) {
        addGroupWithItem(new JiraChangelogItem("Status", "null", "null", "null", status), changeDate);
    }

    private void addGroupWithItem(JiraChangelogItem item, Date created) {
        List<JiraChangelogItem> items = new ArrayList<>();
        items.add(item);
        changelog.add(new JiraChangelogGroup(User.createWithUsername("mmarusic"), created, items));
    }

    private void testJiraIssueViolatesRules(boolean violationExpected, JiraIssue jiraIssueMock) {
        assertResultsIsAsExpected(engine.runCheckOnBugs(CollectionUtils.asSetOf(new Candidate(jiraIssueMock)), checkName),
                checkName, bugId, (violationExpected ? 1 : 0));
    }
    
    private void testJiraIssueViolatesRules(boolean violationExpected) {
        testJiraIssueViolatesRules(violationExpected,createJiraIssueMock(IssueType.UPGRADE));
    }

    private JiraIssue createJiraIssueMock(IssueType type) {
        JiraIssue mock = (JiraIssue) MockUtils.mockJiraIssue(bugId, "A Summary...");
        Mockito.when(mock.getType()).thenReturn(type);
        Mockito.when(mock.getChangelog()).thenReturn(changelog);
        Mockito.when(mock.getStatus()).thenReturn(issueStatus);
        Mockito.when(mock.getSprintRelease()).thenReturn("EAP 7.0.3");
        return mock;
    }
}