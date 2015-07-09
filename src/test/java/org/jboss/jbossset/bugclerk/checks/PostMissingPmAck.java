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

import java.util.TreeSet;

import org.jboss.jbossset.bugclerk.AbstractCheckRunner;
import org.jboss.jbossset.bugclerk.Candidate;
import org.jboss.jbossset.bugclerk.MockUtils;
import org.jboss.jbossset.bugclerk.checks.utils.BugClerkMockingHelper;
import org.jboss.jbossset.bugclerk.checks.utils.CollectionUtils;
import org.jboss.jbossset.bugclerk.checks.utils.DateUtils;
import org.jboss.pull.shared.connectors.bugzilla.Bug;
import org.jboss.pull.shared.connectors.bugzilla.Bug.Status;
import org.jboss.pull.shared.connectors.bugzilla.Comment;
import org.jboss.pull.shared.connectors.common.Flag;
import org.junit.Test;
import org.mockito.Mockito;

public class PostMissingPmAck extends AbstractCheckRunner {

    protected Bug testSpecificStubbingForBug(Bug mock) {
        Mockito.when(mock.getStatus()).thenReturn(Status.POST.toString());
        return mock;
    }

    @Test
    public void violationIfPostAndThreeWeeksAgo() {
        final int bugId = 143794;
        final Bug mock = MockUtils.mockBug(bugId, "summary");
        Mockito.when(mock.getLastModified()).thenReturn(DateUtils.threeWeeksAgo());

        assertResultsIsAsExpected( engine.runCheckOnBugs(checkName, CollectionUtils.asSetOf(new Candidate(testSpecificStubbingForBug(mock),new TreeSet<Comment>())) ), checkName, bugId );
    }


    @Test
    public void noViolationIfTwoWeeksOld() {
        final int bugId = 143794;
        final Bug mock = MockUtils.mockBug(bugId, "summary");
        Mockito.when(mock.getLastModified()).thenReturn(DateUtils.twoWeeksAgo());

        assertResultsIsAsExpected(engine.runCheckOnBugs(checkName, CollectionUtils.asSetOf(new Candidate(testSpecificStubbingForBug(mock),new TreeSet<Comment>())) ), checkName, bugId,0);
    }

    @Test
    public void noViolationIfNoPR() {
        final int bugId = 143794;
        final Bug mock = MockUtils.mockBug(bugId, "summary");
        Mockito.when(mock.getLastModified()).thenReturn(DateUtils.threeWeeksAgo());

        assertResultsIsAsExpected(engine.runCheckOnBugs(checkName, CollectionUtils.asSetOf(new Candidate(mock,new TreeSet<Comment>())) ), checkName, bugId,0);
    }

    @Test
    public void noViolationIfPmAcked() {
        final int bugId = 143794;
        final Bug mock = MockUtils.mockBug(bugId, "summary");
        Mockito.when(mock.getLastModified()).thenReturn(DateUtils.threeWeeksAgo());
        Mockito.when(mock.getFlags()).thenReturn(BugClerkMockingHelper.createFlag(PM_ACK_FLAG, Flag.Status.POSITIVE));


        assertResultsIsAsExpected(engine.runCheckOnBugs(checkName, CollectionUtils.asSetOf(new Candidate(mock,new TreeSet<Comment>())) ), checkName, bugId,0);
    }

}
