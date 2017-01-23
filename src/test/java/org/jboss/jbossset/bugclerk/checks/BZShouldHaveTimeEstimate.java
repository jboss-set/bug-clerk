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

import java.util.Optional;

import org.jboss.jbossset.bugclerk.AbstractCheckRunner;
import org.jboss.jbossset.bugclerk.Candidate;
import org.jboss.jbossset.bugclerk.MockUtils;
import org.jboss.jbossset.bugclerk.checks.utils.CollectionUtils;
import org.jboss.set.aphrodite.domain.Flag;
import org.jboss.set.aphrodite.domain.FlagStatus;
import org.jboss.set.aphrodite.domain.Issue;
import org.jboss.set.aphrodite.domain.IssueEstimation;
import org.jboss.set.aphrodite.domain.IssueStatus;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class BZShouldHaveTimeEstimate extends AbstractCheckRunner {

    private Issue mock;

    private final String POST_OR_MODIFIED = "_PostOrModified";
    private final String DEV_ACKED = "_DevAcked";

    private String bugId = "12345";

    @Test
    public void devFlagSetButNoTimeEstimate() {
        final String checkName = this.checkName + DEV_ACKED;
        mock.getStage().getStateMap().put(Flag.DEV, FlagStatus.SET);
        noTimeEstimate();
        assertResultsIsAsExpected(engine.runCheckOnBugs(CollectionUtils.asSetOf(new Candidate(mock)), checkName), checkName, bugId, 1);
    }

    private void noTimeEstimate() {
        Mockito.when(mock.getEstimation()).thenReturn(Optional.of(new IssueEstimation(0, 0)));
    }

    @Test
    public void postButNoTimeEstimate() {
        final String checkName = this.checkName + POST_OR_MODIFIED;
        Mockito.when(mock.getStatus()).thenReturn(IssueStatus.POST);
        noTimeEstimate();
        assertResultsIsAsExpected(engine.runCheckOnBugs(CollectionUtils.asSetOf(new Candidate(mock)), checkName), checkName, bugId,1);
    }

    @Test
    public void falsePositiveWithPost() {
        final String checkName = this.checkName + POST_OR_MODIFIED;
        Mockito.when(mock.getStatus()).thenReturn(IssueStatus.POST);        
        assertResultsIsAsExpected(engine.runCheckOnBugs(CollectionUtils.asSetOf(new Candidate(mock)), checkName), checkName, bugId, 0);
    }

    @Test
    public void falsePositiveWithDevAck() {
        final String checkName = this.checkName + DEV_ACK_FLAG;
        assertResultsIsAsExpected(engine.runCheckOnBugs(CollectionUtils.asSetOf(new Candidate(mock)), checkName), checkName, bugId, 0);
    }

    @Before
    public void prepareMock() {        
        mock = MockUtils.mockBug(this.bugId, "summary");
        Mockito.when(mock.getEstimation()).thenReturn(Optional.of(new IssueEstimation(3, 3)));
        mock.getStage().getStateMap().put(Flag.DEV, FlagStatus.ACCEPTED);
    }
}
