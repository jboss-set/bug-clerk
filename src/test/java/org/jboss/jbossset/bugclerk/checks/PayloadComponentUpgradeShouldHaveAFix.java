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

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jboss.jbossset.bugclerk.AbstractCheckRunner;
import org.jboss.jbossset.bugclerk.Candidate;
import org.jboss.jbossset.bugclerk.MockUtils;
import org.jboss.jbossset.bugclerk.checks.utils.CollectionUtils;
import org.jboss.set.aphrodite.domain.Issue;
import org.jboss.set.aphrodite.domain.IssueType;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class PayloadFixesMustHaveComponentUpgrade extends AbstractCheckRunner {

    private Issue mock;
    private Issue payloadTracker;
    private final String bugId = "143794";
    private final String payloadId = "143795";

    private final List<String> checknames = Arrays.asList(checkName, "IndexPayloadTRackerByURL", "IndexIssueByURL");

    @Before
    public void prepareBugMock() {
        URL mockUrl = MockUtils.buildURL(bugId);
        URL payloadTrackerUrl = MockUtils.buildURL(payloadId);

        mock = MockUtils.mockBug(mockUrl, "summary");
        payloadTracker = MockUtils.mockBug(payloadTrackerUrl, "EAP 6.6.6 - Payload Tracker");
        Mockito.when(payloadTracker.getDependsOn()).thenReturn(CollectionUtils.asListOf(mockUrl));
        Mockito.when(mock.getBlocks()).thenReturn(CollectionUtils.asListOf(payloadTrackerUrl));
    }

    @Test
    public void violationFixInPayloadButNoComponentUpgradeOnFix() {
        assertResultsIsAsExpected(
                engine.runChecksOnBugs(CollectionUtils.asSetOf(new Candidate(mock), new Candidate(payloadTracker)), checknames), checkName,bugId);
    }

    @Test
    public void noViolationIfFixInPayloadHasComponentUpgradeOnFix() {
        URL payloadTrackerUrl = MockUtils.buildURL(payloadId);
        URL componentUpgrade = MockUtils.buildURL("137458459");

        Issue componentUpgradeIssue = MockUtils.mockBug(componentUpgrade, "Component Upgrade");
        Mockito.when(componentUpgradeIssue.getType()).thenReturn(IssueType.UPGRADE);
        Mockito.when(mock.getBlocks()).thenReturn(CollectionUtils.asListOf(payloadTrackerUrl, componentUpgrade));
        assertResultsIsAsExpected(engine.runChecksOnBugs(CollectionUtils.asSetOf(new Candidate(mock), new Candidate(payloadTracker), new Candidate(componentUpgradeIssue)), checknames), checkName,bugId, 0);
    }

    @Test
    public void violationIfFixInPayloadHasBlockerButNoComponentUpgradeOnFix() {
        URL payloadTrackerUrl = MockUtils.buildURL(payloadId);
        URL componentUpgrade = MockUtils.buildURL("137458459");

        Issue componentUpgradeIssue = MockUtils.mockBug(componentUpgrade, "Component Upgrade");
        Mockito.when(componentUpgradeIssue.getType()).thenReturn(IssueType.BUG);
        Mockito.when(mock.getBlocks()).thenReturn(CollectionUtils.asListOf(payloadTrackerUrl, componentUpgrade));
        assertResultsIsAsExpected(engine.runChecksOnBugs(CollectionUtils.asSetOf(new Candidate(mock), new Candidate(payloadTracker), new Candidate(componentUpgradeIssue)), checknames), checkName,bugId);
    }

    @Test
    public void noViolationIfFixNotInPayload() {
        Mockito.when(mock.getBlocks()).thenReturn(new ArrayList<URL>(0));
        assertResultsIsAsExpected(engine.runChecksOnBugs(CollectionUtils.asSetOf(new Candidate(mock), new Candidate(payloadTracker)), checknames), checkName,bugId, 0);
    }
}
