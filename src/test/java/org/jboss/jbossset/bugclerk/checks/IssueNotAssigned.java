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
import static org.jboss.jbossset.bugclerk.checks.utils.BugClerkMockingHelper.buildTestSubjectWithComments;

import java.util.Date;
import java.util.Optional;

import org.jboss.jbossset.bugclerk.AbstractCheckRunner;
import org.jboss.jbossset.bugclerk.Candidate;
import org.jboss.jbossset.bugclerk.MockUtils;
import org.jboss.jbossset.bugclerk.checks.utils.CollectionUtils;
import org.jboss.set.aphrodite.domain.Issue;
import org.jboss.set.aphrodite.domain.IssueStatus;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class IssueNotAssigned extends AbstractCheckRunner {

    private Issue mock;
    private final String bugId = "143794";

    @Before
    public void prepareBugMock() {
        mock = MockUtils.mockBug(bugId, "summary");
        Mockito.when(mock.getStatus()).thenReturn(IssueStatus.NEW);
    }

    @Test
    public void violationBZOlderThanAMonth() {
        assertResultsIsAsExpected(
                engine.runCheckOnBugs(CollectionUtils.asSetOf(new Candidate(mock)), checkName + "_CreationDate"), checkName,
                bugId);
    }

    @Test
    public void bzHasAlreadyFiveComments() {
        Mockito.when(mock.getCreationTime()).thenReturn(Optional.of(new Date()));
        assertResultsIsAsExpected(engine.runCheckOnBugs(buildTestSubjectWithComments(mock, "first comment", "second one", "third one", "fourth one", "fifth one"),
                checkName + "_CommentSize"),
                checkName, bugId);
    }

    @Test
    public void bzHasAlreadyPR() {
        Mockito.when(mock.getCreationTime()).thenReturn(Optional.of(new Date()));
        assertResultsIsAsExpected(engine.runCheckOnBugs(buildTestSubjectWithComments(mock, "first comment", "has PR: https://github.com/jbossas/jboss-eap/pull/1640"),
                checkName + "_PR"),
                checkName, bugId);
    }

    @Test
    public void bzLessThanAMonthNoViolation() {
        Mockito.when(mock.getStatus()).thenReturn(IssueStatus.NEW);
        Mockito.when(mock.getCreationTime()).thenReturn(Optional.of(new Date()));
        assertResultsIsAsExpected(engine.runCheckOnBugs(CollectionUtils.asSetOf(new Candidate(mock)), checkName), checkName,
                bugId, 0);
    }
}
