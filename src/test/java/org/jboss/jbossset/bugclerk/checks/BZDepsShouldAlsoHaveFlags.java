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
import static org.jboss.jbossset.bugclerk.checks.utils.BugClerkMockingHelper.createAllThreeFlagsAs;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.jboss.jbossset.bugclerk.AbstractCheckRunner;
import org.jboss.jbossset.bugclerk.Candidate;
import org.jboss.jbossset.bugclerk.MockUtils;
import org.jboss.jbossset.bugclerk.Violation;
import org.jboss.pull.shared.connectors.bugzilla.Bug;
import org.jboss.pull.shared.connectors.bugzilla.Bug.Status;
import org.jboss.pull.shared.connectors.bugzilla.Comment;
import org.jboss.pull.shared.connectors.common.Flag;
import org.junit.Test;
import org.mockito.Mockito;

public class BZDepsShouldAlsoHaveFlags extends AbstractCheckRunner {

    @Test
    public void bzOnModifiedAndHasDevFlag() {
        final int bugId = 143794;
        final int dependencyId = 14380;
        Bug payload = MockUtils.mockBug(bugId, "payload bug");
        Bug dependency = MockUtils.mockBug(dependencyId, "dependency payload");

        Collection<Candidate> mocks = buildCollectionOfCandidates(payload, dependency);

        Mockito.when(payload.getFlags()).thenReturn(createAllThreeFlagsAs(Flag.Status.POSITIVE));
        Mockito.when(payload.getDependsOn()).thenReturn(idsAsIntegerSet(14380,158690));

        Collection<Violation> violations = engine.runCheckOnBugs(checkName, mocks);
        for ( Violation v : violations ) {
            System.out.println(v);
        }
        assertThat(violations.size(), is(1));
    }

    private static final Collection<Candidate> buildCollectionOfCandidates(Bug ... bugs) {
        Collection<Candidate> mocks = new ArrayList<Candidate>(bugs.length);
        for ( Bug bug : bugs ) {
            mocks.add(new Candidate(bug, NO_COMMENTS));
        }
        return mocks;
    }

    private Set<Integer> idsAsIntegerSet(int ...ids) {
        Set<Integer> set = new HashSet<Integer>(ids.length);
        for ( int id : ids ) {
            set.add(id);
        }
        return set;
    }

    private Collection<Candidate> buildTestSubjectWithComment(int bugId, String comment) {
        SortedSet<Comment> comments = new TreeSet<Comment>();
        comments.add(MockUtils.mockComment(0, comment, bugId));
        return createListForOneCandidate(new Candidate(MockUtils.mockBug(bugId, "summary"), comments));
    }

    protected Collection<Candidate> createListForOneCandidate(Candidate candidate) {
        Collection<Candidate> candidates = new ArrayList<Candidate>(1);
        candidates.add(candidate);
        return candidates;
    }

    private Bug testSpecificStubbingForBug(Bug mock) {
        Mockito.when(mock.getStatus()).thenReturn(Status.POST.toString());

        List<Flag> flags = new ArrayList<Flag>(1);
        Flag flag = new Flag("jboss-eap-6.4.0", "setter?", Flag.Status.POSITIVE);
        flags.add(flag);

        Mockito.when(mock.getFlags()).thenReturn(flags);
        return mock;
    }

}
