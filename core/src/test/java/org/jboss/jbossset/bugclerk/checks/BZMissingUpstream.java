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

import org.jboss.jbossset.bugclerk.AbstractCheckRunner;
import org.jboss.jbossset.bugclerk.Candidate;
import org.jboss.jbossset.bugclerk.MockUtils;
import org.jboss.jbossset.bugclerk.checks.utils.CollectionUtils;
import org.jboss.jbossset.bugclerk.utils.URLUtils;
import org.jboss.set.aphrodite.config.TrackerType;
import org.jboss.set.aphrodite.domain.Comment;
import org.jboss.set.aphrodite.domain.Issue;
import org.junit.Test;
import org.mockito.Mockito;

public class BZMissingUpstream extends AbstractCheckRunner {

    @Test
    public void bzIssueButNoDeps() {
        final String checkName = super.checkName + "_NoDeps";
        final String issueId = "14380";

        Issue mock = MockUtils.mockBzIssue(issueId, "BZ issue with no deps");

        assertResultsIsAsExpected(engine.runCheckOnBugs(
                CollectionUtils.asSetOf(new Candidate(mock)), checkName), checkName, issueId);
    }

    @Test
    public void bzHasDepsIncludingOneJIRAissue() {
        final String checkName = super.checkName + "_WithDeps";
        final String bzId = "147586";
        final String upstreamJiraIssueId = "https://" + RulesHelper.JIRA_TRACKER_HOSTNAME + "/JBEAP-666";

        Issue jiraUpstreamIssue = MockUtils.mockJiraIssue(upstreamJiraIssueId, "upstream issue");
        Mockito.when(jiraUpstreamIssue.getURL()).thenReturn(URLUtils.createURLFromString(upstreamJiraIssueId));
        Mockito.when(jiraUpstreamIssue.getTrackerType()).thenReturn(TrackerType.JIRA);

        Issue bzIssue = MockUtils.mockBzIssue(bzId, "BZ issue");
        Mockito.when(bzIssue.getDependsOn()).thenReturn(CollectionUtils.asListOf(URLUtils.createURLFromString(upstreamJiraIssueId)));
        assertResultsIsAsExpected(engine.runCheckOnBugs(CollectionUtils.buildCollectionOfCandidates(bzIssue, jiraUpstreamIssue), checkName), checkName, bzId,0);
    }

    @Test
    public void bzHasDepsButNoJIRAissue() {
        final String checkName = super.checkName + "_WithDeps";
        final String bzId = "111";
        final String bzDependencyId = "666";

        Issue bzDependency = MockUtils.mockBzIssue(bzDependencyId, "other BZ deps");

        Issue bzIssue = MockUtils.mockBzIssue(bzId, "BZ issue");
        Mockito.when(bzIssue.getDependsOn()).thenReturn(CollectionUtils.asListOf(MockUtils.buildURL(bzDependencyId)));

        assertResultsIsAsExpected(engine.runCheckOnBugs(CollectionUtils.buildCollectionOfCandidates(bzIssue, bzDependency), checkName), checkName, bzId);
    }

    @Test
    public void noUpstreamButExplanationInComment() {
        final String checkName = super.checkName + "_WithDeps";
        final String bzId = "111";
        final String bzDependencyId = "666";

        Issue bzDependency = MockUtils.mockBzIssue(bzDependencyId, "other BZ deps");

        Issue bzIssue = MockUtils.mockBzIssue(bzId, "BZ issue");
        Mockito.when(bzIssue.getDependsOn()).thenReturn(CollectionUtils.asListOf(MockUtils.buildURL(bzDependencyId)));

        Comment comment = MockUtils.mockComment("0", "There is no upstream issue required because...", bzId);

        Mockito.when(bzIssue.getComments()).thenReturn(CollectionUtils.asListOf(comment));

        assertResultsIsAsExpected(engine.runCheckOnBugs(CollectionUtils.buildCollectionOfCandidates(bzIssue, bzDependency), checkName), checkName, bzId,0);
    }
}
