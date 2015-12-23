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

import static org.hamcrest.CoreMatchers.is;
import static org.jboss.jbossset.bugclerk.checks.utils.AssertsHelper.assertResultsIsAsExpected;
import static org.jboss.jbossset.bugclerk.checks.utils.BugClerkMockingHelper.buildTestSubjectWithComment;
import static org.junit.Assert.assertThat;

import java.util.Collection;

import org.jboss.jbossset.bugclerk.AbstractCheckRunner;
import org.jboss.jbossset.bugclerk.Candidate;
import org.jboss.jbossset.bugclerk.MockUtils;
import org.jboss.jbossset.bugclerk.checks.utils.CollectionUtils;
import org.jboss.set.aphrodite.domain.Flag;
import org.jboss.set.aphrodite.domain.FlagStatus;
import org.jboss.set.aphrodite.domain.Issue;
import org.jboss.set.aphrodite.domain.IssueStatus;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class BZShouldHaveDevAckFlag extends AbstractCheckRunner {

    private Issue mock;

    @Test
    public void bzOnPostButNoDevFlag() {
        assertResultsIsAsExpected(
                engine.runCheckOnBugs(checkName, CollectionUtils.asSetOf(new Candidate(mock))),
                checkName, mock.getTrackerId().get());
    }

    @Test
    public void bzOnModifiedButNoDevFlag() {
        final String payload = "Well; it does seems like one forgot the PR here.";
        final String bugId = "143794";
        Collection<Candidate> mocks = buildTestSubjectWithComment(bugId, payload);
        mocks.iterator().next().getBug().getStage().getStateMap().put(Flag.QE, FlagStatus.NO_SET);
        Mockito.when(mocks.iterator().next().getBug().getStatus()).thenReturn(IssueStatus.MODIFIED);
        assertResultsIsAsExpected(engine.runCheckOnBugs(checkName, mocks), checkName, bugId);
    }

    @Test
    public void bzOnModifiedAndHasDevFlag() {
        final String payload = "Well; it does seems like one forgot the PR here.";
        final String bugId = "143794";
        Collection<Candidate> mocks = buildTestSubjectWithComment(bugId, payload);
        mocks.iterator().next().getBug().getStage().getStateMap().put(Flag.DEV, FlagStatus.ACCEPTED);
        Mockito.when(mocks.iterator().next().getBug().getStatus()).thenReturn(IssueStatus.MODIFIED);
        assertThat(engine.runCheckOnBugs(checkName, mocks).size(), is(0));
    }

    @Test
    public void bzOnModifiedAndHasNoFlags() {
        final String payload = "Well; it does seems like one forgot the PR here.";
        final String bugId = "143794";
        Collection<Candidate> mocks = buildTestSubjectWithComment(bugId, payload);
        Mockito.when(mocks.iterator().next().getBug().getStatus()).thenReturn(IssueStatus.MODIFIED);
        assertResultsIsAsExpected(engine.runCheckOnBugs(checkName, mocks), checkName, bugId);
    }

    @Test
    public void bzOnPostAndHasDevFlag() {
        final String payload = "Well; it does seems like one forgot the PR here.";
        Collection<Candidate> mocks = buildTestSubjectWithComment("143794", payload);
        mocks.iterator().next().getBug().getStage().getStateMap().put(Flag.DEV, FlagStatus.ACCEPTED);
        Mockito.when(mocks.iterator().next().getBug().getStatus()).thenReturn(IssueStatus.POST);
        assertThat(engine.runCheckOnBugs(checkName, mocks).size(), is(0));
    }

    @Before
    public void testSpecificStubbingForBug() {
        mock = MockUtils.mockBug("12345", "summary");
        Mockito.when(mock.getStatus()).thenReturn(IssueStatus.POST);
    }
}
