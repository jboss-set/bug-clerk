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

public class FixVersionChangesDuringSprint extends AbstractCheckRunner {
    private String bugId;
    private List<JiraChangelogGroup> changelog;
    private User authorOfChange;
    private JiraChangelogGroup lastFixVersionChange;

    @Before
    public void resetMockData() {
        bugId = "143794";
        authorOfChange = User.createWithUsername("ReleaseCoordinator");
        resetChangelog();
    }

    private void resetChangelog() {
        changelog = new ArrayList<>();
    }

    @Test
    public void testAllowedUserChangedFixVersion() {
        createChangelogWithVersionChangeAndSprint();
        addGroupWithFixVerChangeDuringSprint(new GregorianCalendar(2001, 1, 1).getTime());
        testFixVersionChangeDoneByAllowedUser(true);
    }

    private void createChangelogWithVersionChangeAndSprint() {
        resetChangelog();
        addGroupWithItem(new JiraChangelogItem("Fix Version", "null", "null", "12331152", "7.0.3.CR1"),
                new GregorianCalendar(1999, 1, 1).getTime());
        addGroupWithItem(new JiraChangelogItem("Sprint", "null", "null", "5086", "EAP 7.0.3"),
                new GregorianCalendar(2000, 1, 1).getTime());
    }

    private void addGroupWithItem(JiraChangelogItem item, Date created) {
        changelog.add(createGroupWithItem(item, created));
    }

    private void addGroupWithFixVerChangeDuringSprint(Date created) {
        lastFixVersionChange = createGroupWithItem(new JiraChangelogItem("Fix Version", "null", "null", "12331152", "7.0.3.CR2"),
                created);
        changelog.add(lastFixVersionChange);
    }

    private JiraChangelogGroup createGroupWithItem(JiraChangelogItem item, Date created) {
        List<JiraChangelogItem> items = new ArrayList<>();
        items.add(item);
        return new JiraChangelogGroup(authorOfChange, created, items);
    }

    private void testFixVersionChangeDoneByAllowedUser(boolean doneByAllowedUser) {
        JiraIssue jiraIssueMock = mockIssue();

        JiraChangelogGroup fixVersionChangeGroup = JiraChangelogHelpers.getLastFixVersionChangeDuringSprint(jiraIssueMock);
        assertEquals(fixVersionChangeGroup, lastFixVersionChange);

        int numberViolationExpected = doneByAllowedUser ? 0 : 1;
        assertResultsIsAsExpected(engine.runCheckOnBugs(CollectionUtils.asSetOf(new Candidate(jiraIssueMock)), checkName), checkName, bugId, numberViolationExpected);
    }

    private JiraIssue mockIssue() {
        JiraIssue jiraIssueMock = MockUtils.mockJira(bugId, "A Summary...");
        Mockito.when(jiraIssueMock.getSprintRelease()).thenReturn("EAP 7.0.3");
        Mockito.when(jiraIssueMock.getChangelog()).thenReturn(changelog);
        return jiraIssueMock;
    }

    @Test
    public void testNOTAllowedUserChangedFixVersion() {
        createChangelogWithVersionChangeAndSprint();
        addGroupWithFixVerChangeDuringSprint(new GregorianCalendar(2001, 1, 1).getTime());

        authorOfChange = User.createWithUsername("NotAllowedUser");
        addGroupWithFixVerChangeDuringSprint(new GregorianCalendar(2002, 1, 1).getTime());

        testFixVersionChangeDoneByAllowedUser(false);
    }

    @Test
    public void testAllowedUserChangedFixVersionAfterNotAllowedUserChangedIt() {
        createChangelogWithVersionChangeAndSprint();
        authorOfChange = User.createWithUsername("NotAllowedUser");
        addGroupWithFixVerChangeDuringSprint(new GregorianCalendar(2001, 1, 1).getTime());

        authorOfChange = User.createWithUsername("ReleaseCoordinator");
        addGroupWithFixVerChangeDuringSprint(new GregorianCalendar(2002, 1, 1).getTime());

        testFixVersionChangeDoneByAllowedUser(true);
    }
}