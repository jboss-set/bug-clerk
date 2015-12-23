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

    @Test
    public void devFlagSetButNoTimeEstimate() {
        mock.getStage().getStateMap().put(Flag.DEV, FlagStatus.SET);
        noTimeEstimate();
        assertThat(engine.runCheckOnBugs(checkName + DEV_ACKED, CollectionUtils.asSetOf(new Candidate(mock))).size(), is(1));
    }

    private void noTimeEstimate() {
        Mockito.when(mock.getEstimation()).thenReturn(Optional.of(new IssueEstimation(0, 0)));
    }

    @Test
    public void postButNoTimeEstimate() {
        Mockito.when(mock.getStatus()).thenReturn(IssueStatus.POST);
        noTimeEstimate();
        assertThat(engine.runCheckOnBugs(checkName + POST_OR_MODIFIED, CollectionUtils.asSetOf(new Candidate(mock))).size(),
                is(1));
    }

    @Test
    public void falsePositiveWithPost() {
        Mockito.when(mock.getStatus()).thenReturn(IssueStatus.POST);
        assertThat(engine.runCheckOnBugs(checkName + POST_OR_MODIFIED, CollectionUtils.asSetOf(new Candidate(mock))).size(),
                is(0));
    }

    @Test
    public void falsePositiveWithDevAck() {
        assertThat(engine.runCheckOnBugs(checkName + DEV_ACK_FLAG, CollectionUtils.asSetOf(new Candidate(mock))).size(), is(0));
    }

    @Before
    public void prepareMock() {
        mock = MockUtils.mockBug("12345", "summary");
        Mockito.when(mock.getEstimation()).thenReturn(Optional.of(new IssueEstimation(3, 3)));
        mock.getStage().getStateMap().put(Flag.DEV, FlagStatus.ACCEPTED);
    }
}
