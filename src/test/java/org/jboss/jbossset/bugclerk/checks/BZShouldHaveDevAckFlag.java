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
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jboss.jbossset.bugclerk.AbstractCheckRunner;
import org.jboss.jbossset.bugclerk.Candidate;
import org.jboss.pull.shared.connectors.bugzilla.Bug;
import org.jboss.pull.shared.connectors.bugzilla.Bug.Status;
import org.jboss.pull.shared.connectors.common.Flag;
import org.junit.Test;
import org.mockito.Mockito;

public class BZShouldHaveDevAckFlag extends AbstractCheckRunner {

    @Test
    public void bzOnPostButNoDevFlag() {
        final String payload = "Well; it does seems like one forgot the PR here.";
        final int bugId = 143794;
        assertResultsIsAsExpected(engine.runCheckOnBugs(checkName, buildTestSubjectWithComment(bugId, payload)), checkName,
                bugId);
    }

    @Test
    public void bzOnModifiedButNoDevFlag() {
        final String payload = "Well; it does seems like one forgot the PR here.";
        final int bugId = 143794;
        Collection<Candidate> mocks = buildTestSubjectWithComment(bugId, payload);
        for (Candidate candidate : mocks) {
            mockCandidate(candidate, Status.MODIFIED, "qa", Flag.Status.UNKNOWN);
        }
        assertResultsIsAsExpected(engine.runCheckOnBugs(checkName, mocks), checkName, bugId);
    }

    @Test
    public void bzOnModifiedAndHasDevFlag() {
        final String payload = "Well; it does seems like one forgot the PR here.";
        final int bugId = 143794;
        Collection<Candidate> mocks = buildTestSubjectWithComment(bugId, payload);
        for (Candidate candidate : mocks) {
            mockCandidate(candidate, Status.MODIFIED, DEV_ACK_FLAG, Flag.Status.UNKNOWN);
        }
        assertThat(engine.runCheckOnBugs(checkName, mocks).size(), is(0));
    }

    @Test
    public void bzOnModifiedAndHasNoFlags() {
        final String payload = "Well; it does seems like one forgot the PR here.";
        final int bugId = 143794;
        Collection<Candidate> mocks = buildTestSubjectWithComment(bugId, payload);
        for (Candidate candidate : mocks) {
            Mockito.when(candidate.getBug().getStatus()).thenReturn(Status.MODIFIED.toString());
        }
        assertResultsIsAsExpected(engine.runCheckOnBugs(checkName, mocks), checkName, bugId);
    }

    @Test
    public void bzOnPostAndHasDevFlag() {
        final String payload = "Well; it does seems like one forgot the PR here.";
        final int bugId = 143794;
        Collection<Candidate> mocks = buildTestSubjectWithComment(bugId, payload);
        for (Candidate candidate : mocks) {
            mockCandidate(candidate, Status.POST, DEV_ACK_FLAG, Flag.Status.UNKNOWN);
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

    @Override
    protected Bug testSpecificStubbingForBug(Bug mock) {
        Mockito.when(mock.getStatus()).thenReturn(Status.POST.toString());

        List<Flag> flags = new ArrayList<Flag>(1);
        Flag flag = new Flag("jboss-eap-6.4.0", "setter?", Flag.Status.POSITIVE);
        flags.add(flag);

        Mockito.when(mock.getFlags()).thenReturn(flags);
        return mock;
    }

}
