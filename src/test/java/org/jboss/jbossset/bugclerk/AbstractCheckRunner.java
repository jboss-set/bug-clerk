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
package org.jboss.jbossset.bugclerk;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.SortedSet;
import java.util.TreeSet;

import org.jboss.pull.shared.connectors.bugzilla.Bug;
import org.jboss.pull.shared.connectors.bugzilla.Comment;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public abstract class AbstractCheckRunner {

    protected RuleEngine engine;
    protected final String checkName;

    public AbstractCheckRunner() {
        checkName = this.getClass().getSimpleName();
    }

    @Before
    public void initRuleEngine() {
        this.engine = new RuleEngine(BugClerk.KIE_SESSION);
    }

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @After
    public void shutdownRuleEngine() {
        this.engine.shutdownRuleEngine();
    }

    protected Bug createMockedBug(int bugId) {
        Bug mock = Mockito.mock(Bug.class);
        Mockito.when(mock.getId()).thenReturn(bugId);
        Mockito.when(mock.getSummary()).thenReturn("summary");
        return testSpecificStubbingForBug(mock);
    }

    protected Bug testSpecificStubbingForBug(Bug bug) {
        return bug;
    }

    protected Comment testSpecificStubbingForComment(Comment comment) {
        return comment;
    }

    protected Comment createMockedComment(int id, String text, int bugId) {
        return testSpecificStubbingForComment(MockUtils.mockComment(id, text, bugId));
    }

    protected Collection<Candidate> buildTestSubjectWithComment(int bugId, String comment) {
        SortedSet<Comment> comments = new TreeSet<Comment>();
        comments.add(createMockedComment(0, comment, bugId));
        return createListForOneCandidate(new Candidate(createMockedBug(bugId), comments));
    }

    protected Collection<Candidate> createListForOneCandidate(Candidate candidate) {
        Collection<Candidate> candidates = new ArrayList<Candidate>(1);
        candidates.add(candidate);
        return candidates;
    }

    protected Collection<Candidate> buildTestSubject(int bugId) {
        return createListForOneCandidate ( new Candidate(createMockedBug(bugId), new TreeSet<Comment>() ));
    }

    protected Collection<Candidate> filterCandidateOut(Collection<Candidate> candidates) {
        for ( Candidate candidate : candidates ) {
            candidate.setFiltered(true);
        }
        return candidates;
    }

    protected void assertResultsIsAsExpected(Collection<Violation> violations, String checkname, int bugId) {
        assertThat(violations.size(), is(1));
        for ( Violation v : violations ) {
            assertThat(v.getBug().getId(), is(bugId));
            assertThat(v.getCheckName(), is(checkname));
        }
    }


    @Test
    public void filteredCandidateShouldBeIgnored() {
        final String payload = "Well; it does seems like one forgot the PR here.";
        final int bugId = 143794;

        assertThat( engine.runCheckOnBugs(checkName, filterCandidateOut(buildTestSubjectWithComment(bugId, payload))).size(), is(0) );
    }

}
