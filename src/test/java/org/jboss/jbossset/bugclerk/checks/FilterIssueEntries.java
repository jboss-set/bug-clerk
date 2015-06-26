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
import static org.mockito.Matchers.any;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.jboss.jbossset.bugclerk.AbstractCheckRunner;
import org.jboss.jbossset.bugclerk.Candidate;
import org.jboss.jbossset.bugclerk.MockUtils;
import org.jboss.jbossset.bugclerk.checks.utils.CollectionUtils;
import org.jboss.jbossset.bugclerk.utils.URLUtils;
import org.jboss.pull.shared.connectors.bugzilla.Bug;
import org.jboss.pull.shared.connectors.bugzilla.Bug.Status;
import org.jboss.pull.shared.connectors.bugzilla.Comment;
import org.junit.Before;
import org.junit.Test;
import org.kohsuke.github.GHPullRequest;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

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

    @Test
    public void addPullRequests() throws MalformedURLException {
        final URL url = new URL("https://github.com/jbossas/jboss-eap/pull/2265");
        SortedSet<Comment> comments = new TreeSet<Comment>();
        comments.add(MockUtils.mockComment(1,url.toString(), mock.getId()));
        Mockito.when(githubClient.extractPullRequestsFromText(any(String.class))).thenAnswer(new ExtractPullRequestsFromTextAnswer());

        Collection<Candidate> candidates = engine.filterBugs("AddPullRequests",
                CollectionUtils.asSetOf(new Candidate(mock, comments)));
        assertThat(candidates.size(), is(1));
        List<GHPullRequest> PRs = candidates.iterator().next().getPullRequests();
        assertThat(PRs.size(), is(1));
    }

    class ExtractPullRequestsFromTextAnswer implements Answer<List<GHPullRequest>> {

        @Override
        public List<GHPullRequest> answer(InvocationOnMock invocation) throws Throwable {
            List<GHPullRequest> pullRequests = new ArrayList<GHPullRequest>(0);
            for (String url : URLUtils.extractUrls((String)invocation.getArguments()[0])) {
                pullRequests.add(MockUtils.mockPR(url));
            }
            return pullRequests;

        }

    }

    @Test
    public void scrubPullRequests() throws MalformedURLException {
        final String firstPR = "https://github.com/jbossas/jboss-eap/pull/2265";
        final String secondPR = "http://github.com/jbossas/jboss-eap/pull/2455";
        final String textToScrub = firstPR + "\n\nThis was merged, but there is still this one to merge:" + secondPR + ".";
        Mockito.when(githubClient.extractPullRequestsFromText(any(String.class))).thenAnswer(new ExtractPullRequestsFromTextAnswer());

        final URL url = new URL(textToScrub);
        SortedSet<Comment> comments = new TreeSet<Comment>();
        comments.add(MockUtils.mockComment(1,url.toString(), mock.getId()));

        Collection<Candidate> candidates = engine.filterBugs("AddPullRequests",
                CollectionUtils.asSetOf(new Candidate(mock, comments)));
        assertThat(candidates.size(), is(1));
        List<GHPullRequest> pullRequests = candidates.iterator().next().getPullRequests();
        assertThat(pullRequests.size(), is(2));
    }

    @Test
    public void ignoreNotPullRequestURL() throws MalformedURLException {
        final URL url = new URL("https://svn.jboss.org/jbossas/jboss-eap\n\nThis was merged, but there is still this one to merge: http://github.com/commits/.");
        SortedSet<Comment> comments = new TreeSet<Comment>();
        comments.add(MockUtils.mockComment(1,url.toString(), mock.getId()));

        Collection<Candidate> candidates = engine.filterBugs("AddPullRequests",
                CollectionUtils.asSetOf(new Candidate(mock, comments)));
        assertThat(candidates.size(), is(1));
        List<GHPullRequest> metas = candidates.iterator().next().getPullRequests();
        assertThat(metas.size(), is(0));
    }



    private static void assertThatFilterWorksAsExpected(Collection<Candidate> candidates, boolean isFiltered) {
        assertThat(candidates.size(), is(1));
        assertThat(candidates.iterator().next().isFiltered(), is(isFiltered));
    }

    private static void assertThatFilterWorksAsExpected(Collection<Candidate> candidates) {
        assertThatFilterWorksAsExpected(candidates,true);
    }
}
