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

import java.util.TreeSet;

import org.jboss.jbossset.bugclerk.AbstractCheckRunner;
import org.jboss.jbossset.bugclerk.Candidate;
import org.jboss.jbossset.bugclerk.MockUtils;
import org.jboss.jbossset.bugclerk.checks.utils.CollectionUtils;
import org.jboss.pull.shared.connectors.bugzilla.Bug;
import org.jboss.pull.shared.connectors.bugzilla.Bug.Status;
import org.jboss.pull.shared.connectors.bugzilla.Comment;
import org.junit.Test;
import org.mockito.Mockito;

public class PostMissingPR extends AbstractCheckRunner {

    protected Bug testSpecificStubbingForBug(Bug mock) {
        Mockito.when(mock.getStatus()).thenReturn(Status.POST.toString());
        return mock;
    }

    @Test
    public void violationIfPRappearsInAComment() {
        final int bugId = 143794;
        final Bug mock = MockUtils.mockBug(bugId, "summary");
        Mockito.when(mock.getStatus()).thenReturn(Status.POST.toString());

        assertResultsIsAsExpected( engine.runCheckOnBugs(checkName, CollectionUtils.asSetOf(new Candidate(mock,new TreeSet<Comment>())) ), checkName, bugId );
    }

    @Test
    public void noViolationIfPRappearsInAComment() {
        final String payload = "Where did you find that one?\n \n I copied the wrong one, I was testing for a case and I cherry picked it into my branch and built it, looks like I copied from there, where the has was different.\n \n Wildfly is:\n \n commit 3f0118a695aa7350e63fef395e2b4c61eb3932e4\n Author: Brian Stansberry <brian.stansberry@redhat.com>\n Date: Thu Apr 18 20:24:17 2013 -0500\n \n AS7-6949 Don't read child resource twice; avoid NPE\n \n \n 6.x PR:\n https://github.com/jbossas/jboss-eap/pull/2273\n";

        final Bug mock = MockUtils.mockBug(143794, "summary");
        Mockito.when(mock.getStatus()).thenReturn(Status.POST.toString());


        assertThat(engine.runCheckOnBugs(checkName, buildTestSubjectWithComment(mock, payload)).size(), is(0));
    }

    @Test
    public void noViolationIfCommitAppearsInAComment() {
        final String payload = "PR is there : https://github.com/jbossas/redhat-picketlink/commit/43a7fc4b2bd1438f23b22ffbdcbb341aa45ba885";

        final Bug mock = MockUtils.mockBug(143794, "summary");
        Mockito.when(mock.getStatus()).thenReturn(Status.POST.toString());


        assertThat(engine.runCheckOnBugs(checkName, buildTestSubjectWithComment(mock, payload)).size(), is(0));
    }

}
