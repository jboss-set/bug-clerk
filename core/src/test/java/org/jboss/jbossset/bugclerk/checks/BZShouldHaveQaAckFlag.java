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
import static org.jboss.jbossset.bugclerk.checks.utils.BugClerkMockingHelper.buildTestSubjectWithComment;

import java.util.Collection;

import org.jboss.jbossset.bugclerk.AbstractCheckRunner;
import org.jboss.jbossset.bugclerk.Candidate;
import org.jboss.jbossset.bugclerk.MockUtils;
import org.jboss.set.aphrodite.domain.Flag;
import org.jboss.set.aphrodite.domain.FlagStatus;
import org.jboss.set.aphrodite.domain.Issue;
import org.jboss.set.aphrodite.domain.IssueStatus;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class BZShouldHaveQaAckFlag extends AbstractCheckRunner {

    final String bugId = "143794";
    private Issue mock;

    @Test
    public void bzOnVerifiedButNoQaFlag() {
        final String payload = "Well; it does seems like one forgot the PR here.";
        assertResultsIsAsExpected(engine.runCheckOnBugs(buildTestSubjectWithComment(mock, payload), checkName), checkName, bugId);
    }

    @Test
    public void bzOnVerifiedAndHasNoQaFlag() {
        final String payload = "Well; it does seems like one forgot the PR here.";
        Collection<Candidate> mocks = buildTestSubjectWithComment(bugId, payload);
        Mockito.when(mock.getStatus()).thenReturn(IssueStatus.VERIFIED);
        mock.getStage().getStateMap().put(Flag.QE, FlagStatus.NO_SET);
        assertResultsIsAsExpected(engine.runCheckOnBugs(mocks, checkName), checkName, bugId, 0);
    }

    @Test
    public void bzOnVerifiedAndHasQaFlag() {
        final String payload = "Well; it does seems like one forgot the PR here.";
        Collection<Candidate> mocks = buildTestSubjectWithComment(bugId, payload);
        mock.getStage().getStateMap().put(Flag.QE, FlagStatus.NO_SET);
        assertResultsIsAsExpected(engine.runCheckOnBugs(mocks, checkName), checkName, bugId, 0);
    }

    @Test
    public void bzOnVerifiedAndHasQaFlagPositive() {
        final String payload = "Well; it does seems like one forgot the PR here.";
        Collection<Candidate> mocks = buildTestSubjectWithComment(bugId, payload);
        mock.getStage().getStateMap().put(Flag.QE, FlagStatus.ACCEPTED);
        assertResultsIsAsExpected(engine.runCheckOnBugs(mocks, checkName), checkName, bugId, 0);
    }

    @Before
    public void testSpecificStubbingForBug() {
        mock = MockUtils.mockBzIssue(bugId, "summary");
        Mockito.when(mock.getStatus()).thenReturn(IssueStatus.VERIFIED);
    }
}
