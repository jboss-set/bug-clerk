/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2017, Red Hat, Inc., and individual contributors
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

import org.jboss.jbossset.bugclerk.AbstractCheckRunner;
import org.jboss.jbossset.bugclerk.Candidate;
import org.jboss.jbossset.bugclerk.MockUtils;
import org.jboss.jbossset.bugclerk.checks.utils.CollectionUtils;
import org.jboss.set.aphrodite.issue.trackers.jira.JiraIssue;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.jboss.jbossset.bugclerk.checks.utils.AssertsHelper.assertResultsIsAsExpected;

public class PRLinksNotValid extends AbstractCheckRunner {

    private String summary;
    private String bugId;

    @Before
    public void resetMockData() {
        summary = "A Summary...";
    }

    @Test
    public void testWrongHost() {
        bugId = "JBEAP-4367";

        String pullRequest = "https://badHost.com/aeshell/aesh.git/pull/88";
        test(pullRequest, 1);

    }

    private void test(String pullRequest, int nbViolationExpected) {
        JiraIssue issue = MockUtils.mockJiraIssue(bugId, summary);
        Mockito.when(issue.getSprintRelease()).thenReturn("EAP 7.0.3");

        Mockito.when(issue.getPullRequests()).thenReturn(MockUtils.mockPullRequestsUrls(pullRequest));
        assertResultsIsAsExpected(engine.runCheckOnBugs(CollectionUtils.asSetOf(new Candidate(issue)), checkName),
                checkName, bugId, nbViolationExpected);
    }

    @Test
    public void testNotPRAndNotCommit() {
        bugId = "WFCORE-4367";

        String pullRequest = "https://github.com/aeshell/aesh.git/wrong/88";
        test(pullRequest, 1);

    }

    @Test
    public void testInvalidOrganization() {
        bugId = "WFCORE-4367";

        String pullRequest = "https://github.com/aeshell/aesh.git/pull/88";
        test(pullRequest, 1);
    }

    @Test
    public void testValidOrganization() {
        bugId = "ELY-4367";

        String pullRequest = "https://github.com/aeshell/aesh.git/pull/88";
        test(pullRequest, 0);
    }

    @Test
    public void testValidPR() {
        bugId = "JBEAP-4367";

        String pullRequest = "https://github.com/jbossas/aesh.git/pull/88";
        test(pullRequest, 0);
    }

    @Test
    public void testValidCommit() {
        bugId = "WFLY-4367";

        String pullRequest = "https://github.com/wildfly/aesh.git/commit/8204147a19e1b7c2e137e7e8ddcc66c74ec54088";
        test(pullRequest, 0);
    }

    @Test
    public void testValidCommitWFCORE() {
        bugId = "WFCORE-4367";

        String pullRequest = "https://github.com/wildfly/aesh.git/commit/8204147a19e1b7c2e137e7e8ddcc66c74ec54088";
        test(pullRequest, 0);
    }
}