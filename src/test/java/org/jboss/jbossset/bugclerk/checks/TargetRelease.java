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
import static org.jboss.jbossset.bugclerk.checks.utils.BugClerkMockingHelper.buildTestSubjectWithComment;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.jboss.jbossset.bugclerk.AbstractCheckRunner;
import org.jboss.jbossset.bugclerk.Candidate;
import org.jboss.set.aphrodite.domain.FlagStatus;
import org.jboss.set.aphrodite.domain.Issue;
import org.jboss.set.aphrodite.domain.IssueType;
import org.junit.Test;
import org.mockito.Mockito;

public class TargetRelease extends AbstractCheckRunner {

    private Map<String, FlagStatus> stream;

    @Test
    public void violationIfNoDependsOnAndComponentUpgradeType() {
        final String payload = "Well; it does seems like one forgot the PR here.";
        final String bugId = "143794";
        Collection<Candidate> candidates = buildTestSubjectWithComment(bugId, payload);
        Issue mock = candidates.iterator().next().getBug();
        Mockito.when(mock.getType()).thenReturn(IssueType.UPGRADE);
        stream = new HashMap<String, FlagStatus>();
        stream.put("jboss‑eap‑6.4.z", FlagStatus.ACCEPTED);
        stream.put("jboss‑eap‑6.3.z", FlagStatus.ACCEPTED);
        Mockito.when(mock.getStreamStatus()).thenReturn(stream);
        assertResultsIsAsExpected(engine.runCheckOnBugs(checkName, candidates), checkName, bugId);
    }

    protected Issue testSpecificStubbingForBug(Issue mock) {

        return mock;
    }

}
