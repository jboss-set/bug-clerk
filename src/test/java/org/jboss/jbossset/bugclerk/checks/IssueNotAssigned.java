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

import java.util.Calendar;
import java.util.Date;
import java.util.TreeSet;

import org.jboss.jbossset.bugclerk.AbstractCheckRunner;
import org.jboss.jbossset.bugclerk.Candidate;
import org.jboss.pull.shared.connectors.bugzilla.Bug;
import org.jboss.pull.shared.connectors.bugzilla.Bug.Status;
import org.jboss.pull.shared.connectors.bugzilla.Comment;
import org.junit.Test;
import org.mockito.Mockito;

public class IssueNotAssigned extends AbstractCheckRunner {

    private static Date twoMonthAgo() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -2);
        return cal.getTime();
    }

    @Override
    protected Bug testSpecificStubbingForBug(Bug mock) {
        Mockito.when(mock.getStatus()).thenReturn(Status.NEW.toString());
        Mockito.when(mock.getCreationTime()).thenReturn(new Date());
        return mock;
    }

    @Test
    public void violationBZOlderThanAMonth() {
        final int bugId = 143794;
        Bug mock = createMockedBug(bugId);
        Mockito.when(mock.getStatus()).thenReturn(Status.NEW.toString());
        Mockito.when(mock.getCreationTime()).thenReturn(twoMonthAgo());
        assertResultsIsAsExpected( engine.runCheckOnBugs(checkName + "_CreationDate", createListForOneCandidate(new Candidate(mock, new TreeSet<Comment>()))), checkName, bugId );
    }

    @Test
    public void bzHasAlreadyFiveComments() {
        final int bugId = 143794;
        assertResultsIsAsExpected( engine.runCheckOnBugs(checkName + "_CommentSize", buildTestSubjectWithComments(bugId, "first comment", "second one", "third one", "fourth one", "fifth one")), checkName, bugId);
    }

    @Test
    public void bzHasAlreadyPR() {
        final int bugId = 143794;
        assertResultsIsAsExpected( engine.runCheckOnBugs(checkName + "_PR", buildTestSubjectWithComments(bugId, "first comment", "has PR: https://github.com/jbossas/jboss-eap/pull/1640")), checkName, bugId);
    }



    @Test
    public void bzLessThanAMonthNoViolation() {
        final int bugId = 143794;
        Bug mock = createMockedBug(bugId);
        Mockito.when(mock.getStatus()).thenReturn(Status.NEW.toString());
        Mockito.when(mock.getCreationTime()).thenReturn(new Date());
        assertResultsIsAsExpected( engine.runCheckOnBugs(checkName, createListForOneCandidate(new Candidate(mock, new TreeSet<Comment>()))), checkName, bugId,0);
    }
}
