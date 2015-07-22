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

import java.util.Collection;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.jboss.jbossset.bugclerk.AbstractCheckRunner;
import org.jboss.jbossset.bugclerk.Candidate;
import org.jboss.jbossset.bugclerk.MockUtils;
import org.jboss.jbossset.bugclerk.checks.utils.CollectionUtils;
import org.jboss.pull.shared.connectors.bugzilla.Bug;
import org.jboss.pull.shared.connectors.bugzilla.Bug.Status;
import org.jboss.pull.shared.connectors.bugzilla.Comment;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class FilterIssueEntries extends AbstractCheckRunner {

    private Bug mock;
    private final int bugId = 143794;

    @Before
    public void prepareBugMock() {
        mock = MockUtils.mockBug(bugId, "summary");
        Mockito.when(mock.getType()).thenReturn("Documentation");
    }

    @Test
    public void ignoreDocFeatureBZ() {
        Mockito.when(mock.getSummary()).thenReturn("[Doc Feature] Something doc changes to implement.");
        assertThatFilterWorksAsExpected(engine.filterBugs("IgnoreDocFeatureBZ",
                CollectionUtils.asSetOf(new Candidate(mock, new TreeSet<Comment>()))));

    }

    @Test
    public void ignoreDocumentationTypeBZ() {
        assertThatFilterWorksAsExpected(engine.filterBugs("IgnoreDocumentationTypeBZ",
                CollectionUtils.asSetOf(new Candidate(mock, new TreeSet<Comment>()))));

    }

    @Test
    public void ignoreClosedBZ() {
        Mockito.when(mock.getStatus()).thenReturn(Status.CLOSED.toString());
        assertThatFilterWorksAsExpected(engine.filterBugs("IgnoreClosedBZ",
                CollectionUtils.asSetOf(new Candidate(mock, new TreeSet<Comment>()))));

    }

    @Test
    public void ignoreDuplicateBZ() {
        Mockito.when(mock.getResolution()).thenReturn("DUPLICATE");
        assertThatFilterWorksAsExpected(engine.filterBugs("IgnoreDuplicateBZ",
                CollectionUtils.asSetOf(new Candidate(mock, new TreeSet<Comment>()))));
    }

    @Test
    public void ignoreRpmsBZ() {
        Mockito.when(mock.getSummary()).thenReturn("[RPMs] Something doc changes to implement.");

        assertThatFilterWorksAsExpected(engine.filterBugs("IgnoreRpmsBZ",
                CollectionUtils.asSetOf(new Candidate(mock, new TreeSet<Comment>()))));
    }

    @Test
    public void setIgnoreFlags() {
        final String NO_PR = "PostMissingPR";
        final String COMMUNITY_BZ = "CommunityBZ";
        SortedSet<Comment> comments = new TreeSet<Comment>();
        comments.add(MockUtils.mockComment(1, "BugClerk#" + NO_PR, mock.getId()));
        comments.add(MockUtils.mockComment(2, "BugClerk#" + COMMUNITY_BZ, mock.getId()));

        Collection<Candidate> candidates = engine.filterBugs("SetIgnoreFlags",
                CollectionUtils.asSetOf(new Candidate(mock, comments)));
        assertThat(candidates.size(), is(1));
        Set<String> metas = candidates.iterator().next().getChecksToBeIgnored();
        assertThat(metas.size(), is(2));
        assertThat(metas.contains(NO_PR), is( true));
        assertThat(metas.contains(COMMUNITY_BZ), is(true));
    }

    private static void assertThatFilterWorksAsExpected(Collection<Candidate> candidates, boolean isFiltered) {
        assertThat(candidates.size(), is(1));
        assertThat(candidates.iterator().next().isFiltered(), is(isFiltered));
    }

    private static void assertThatFilterWorksAsExpected(Collection<Candidate> candidates) {
        assertThatFilterWorksAsExpected(candidates,true);
    }
}
