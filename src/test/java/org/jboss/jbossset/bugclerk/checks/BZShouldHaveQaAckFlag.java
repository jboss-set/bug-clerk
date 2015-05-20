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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jboss.jbossset.bugclerk.AbstractCheckRunner;
import org.jboss.jbossset.bugclerk.Candidate;
import org.jboss.jbossset.bugclerk.MockUtils;
import org.jboss.jbossset.bugclerk.Violation;
import org.jboss.jbossset.bugclerk.checks.utils.BugClerkMockingHelper;
import org.jboss.pull.shared.connectors.bugzilla.Bug;
import org.jboss.pull.shared.connectors.bugzilla.Bug.Status;
import org.jboss.pull.shared.connectors.common.Flag;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class BZShouldHaveQaAckFlag extends AbstractCheckRunner {

    final int bugId = 143794;
    private Bug mock;

    @Test
    public void bzOnVerifiedButNoQaFlag() {
        final String payload = "Well; it does seems like one forgot the PR here.";
        Collection<Violation>v = engine.runCheckOnBugs(checkName, buildTestSubjectWithComment(mock, payload));
        assertResultsIsAsExpected(
                v
                ,checkName,bugId);
    }

    @Test
    public void bzOnVeriifiedAndHasQaFlag() {
        final String payload = "Well; it does seems like one forgot the PR here.";

        Collection<Candidate> mocks = buildTestSubjectWithComment(bugId, payload);
        for ( Candidate candidate : mocks ) {
            mockCandidate(candidate, Status.VERIFIED, QA_ACK_FLAG, Flag.Status.UNKNOWN);
        }
        assertThat(engine.runCheckOnBugs(checkName, mocks).size(), is(0));
    }

    @Test
    public void bzOnVerifiedAndHasNoFlags() {
        final String payload = "Well; it does seems like one forgot the PR here.";
        Collection<Candidate> mocks = buildTestSubjectWithComment(bugId, payload);
        for ( Candidate candidate : mocks ) {
            Mockito.when(candidate.getBug().getStatus()).thenReturn(Status.VERIFIED.toString());
        }
        assertResultsIsAsExpected(engine.runCheckOnBugs(checkName, mocks), checkName, bugId);
    }

    @Test
    public void bzOnVerifiedAndHasQaFlag() {
        final String payload = "Well; it does seems like one forgot the PR here.";
        Collection<Candidate> mocks = buildTestSubjectWithComment(bugId, payload);
        for ( Candidate candidate : mocks ) {
            mockCandidate(candidate, Status.VERIFIED, QA_ACK_FLAG, Flag.Status.UNKNOWN);
        }
        assertThat(engine.runCheckOnBugs(checkName, mocks).size(), is(0));
    }

    @Test
    public void bzOnVerifiedAndHasQaFlagPositive() {
        final String payload = "Well; it does seems like one forgot the PR here.";
        Collection<Candidate> mocks = buildTestSubjectWithComment(bugId, payload);
        for ( Candidate candidate : mocks ) {
            mockCandidate(candidate, Status.VERIFIED, QA_ACK_FLAG, Flag.Status.POSITIVE);
        }
        assertThat(engine.runCheckOnBugs(checkName, mocks).size(), is(0));
    }


    private static void mockCandidate(Candidate candidate, Status status, String flagname, Flag.Status flagStatus) {
        Mockito.when(candidate.getBug().getStatus()).thenReturn(status.toString());

        List<Flag> flags = new ArrayList<Flag>(1);
        Flag flag = new Flag(flagname, "setter?", flagStatus);
        flags.add(flag);
        Mockito.when(candidate.getBug().getFlags()).thenReturn(flags);
    }

    @Before
    public void testSpecificStubbingForBug() {
        mock = MockUtils.mockBug(bugId, "summary");
        Mockito.when(mock.getStatus()).thenReturn(Status.VERIFIED.toString());

        List<Flag> flags = new ArrayList<Flag>(1);
        Flag flag = new Flag(BugClerkMockingHelper.PM_ACK_FLAG, "setter?", Flag.Status.NEGATIVE);
        flags.add(flag);

        Mockito.when(mock.getFlags()).thenReturn(flags);
    }

}
