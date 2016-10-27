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

import static org.jboss.jbossset.bugclerk.checks.utils.AssertsHelper.assertNoViolationFound;
import static org.jboss.jbossset.bugclerk.checks.utils.AssertsHelper.assertResultsIsAsExpected;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.jbossset.bugclerk.AbstractCheckRunner;
import org.jboss.jbossset.bugclerk.Candidate;
import org.jboss.jbossset.bugclerk.MockUtils;
import org.jboss.jbossset.bugclerk.checks.utils.CollectionUtils;
import org.jboss.set.aphrodite.domain.FlagStatus;
import org.jboss.set.aphrodite.domain.Issue;
import org.jboss.set.aphrodite.domain.IssueType;
import org.jboss.set.aphrodite.domain.Release;
import org.junit.Test;
import org.mockito.Mockito;

public class ComponentUpgradeTargetReleaseMismatch extends AbstractCheckRunner {

    private static List<Release> buildReleases() {
        return MockUtils.mockReleases("7.1.0", "CR1");
    }

    private static Map<String, FlagStatus> buildStreamStatus() {
        return MockUtils.mockStreamStatus("7.0.z", FlagStatus.ACCEPTED);
    }

    @Test
    public void violationIfComponentUpgradeHasMismatch() {
        final String bugId = "JBEAP-0666";
        final Issue mock = MockUtils.mockBug(bugId, "summary");
        Mockito.when(mock.getType()).thenReturn(IssueType.UPGRADE);
        Mockito.when(mock.getReleases()).thenReturn(buildReleases());
        Mockito.when(mock.getStreamStatus()).thenReturn(buildStreamStatus());

        assertResultsIsAsExpected(engine.runCheckOnBugs(CollectionUtils.asSetOf(new Candidate(mock)), checkName), checkName,
                bugId);
    }

    @Test
    public void noViolationIfNotComponentUpgrade() {
        final String bugId = "JBEAP-0666";
        final Issue mock = MockUtils.mockBug(bugId, "summary");
        Mockito.when(mock.getType()).thenReturn(IssueType.BUG);
        Mockito.when(mock.getReleases()).thenReturn(buildReleases());
        Mockito.when(mock.getStreamStatus()).thenReturn(buildStreamStatus());

        assertNoViolationFound(engine.runCheckOnBugs(CollectionUtils.asSetOf(new Candidate(mock)), checkName), checkName, bugId);
    }

    @Test
    public void noViolationIfIssueHasNoStreamStatus() {
        final String bugId = "JBEAP-0666";
        final Issue mock = MockUtils.mockBug(bugId, "summary");
        Mockito.when(mock.getType()).thenReturn(IssueType.UPGRADE);
        Mockito.when(mock.getReleases()).thenReturn(buildReleases());
        Mockito.when(mock.getStreamStatus()).thenReturn(new HashMap<String,FlagStatus>(0));

        assertNoViolationFound(engine.runCheckOnBugs(CollectionUtils.asSetOf(new Candidate(mock)), checkName), checkName, bugId);
    }
    
    @Test
    public void noViolationIfIssueHasNoReleases() {
        final String bugId = "JBEAP-0666";
        final Issue mock = MockUtils.mockBug(bugId, "summary");
        Mockito.when(mock.getType()).thenReturn(IssueType.UPGRADE);
        Mockito.when(mock.getReleases()).thenReturn(new ArrayList<Release>(0));
        Mockito.when(mock.getStreamStatus()).thenReturn(buildStreamStatus());

        assertNoViolationFound(engine.runCheckOnBugs(CollectionUtils.asSetOf(new Candidate(mock)), checkName), checkName, bugId);
    }

}
