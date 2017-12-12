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
import java.util.Arrays;
import java.util.Collections;
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

public class PayloadComponentUpgradeShouldHaveAFix extends AbstractCheckRunner {

    private Issue mock;
    private Issue payloadTracker;
    private final String bugId = "143794";
    private final String payloadId = "143795";

    private final List<String> checknames = Arrays.asList(checkName, INDEX_PAYLOAD_RULE, INDEX_ISSUE_RULE);

    @Before
    public void prepareBugMock() {
        URL upgradeURL = MockUtils.buildURL(bugId);
        URL payloadTrackerUrl = MockUtils.buildURL(payloadId);

        mock = MockUtils.mockBzIssue(bugId, upgradeURL, "summary");
        Mockito.when(mock.getType()).thenReturn(IssueType.UPGRADE);
        payloadTracker = MockUtils.mockBzIssue(payloadId, payloadTrackerUrl, "EAP 6.6.6 - Payload Tracker");
        Mockito.when(payloadTracker.getType()).thenReturn(IssueType.BUG);
        Mockito.when(payloadTracker.getDependsOn()).thenReturn(CollectionUtils.asListOf(upgradeURL));
        Mockito.when(mock.getBlocks()).thenReturn(CollectionUtils.asListOf(payloadTrackerUrl));
    }

    @Test
    public void violationUpgradeInPayloadButNoFixOnUpgrade() {
        assertResultsIsAsExpected(
                engine.runChecksOnBugs(CollectionUtils.asSetOf(new Candidate(mock), new Candidate(payloadTracker)), checknames),
                checkName, bugId);
    }

    @Test
    public void noViolationIfNotAnUpgrade() {
        Mockito.when(mock.getType()).thenReturn(IssueType.BUG);
        assertResultsIsAsExpected(
                engine.runChecksOnBugs(CollectionUtils.asSetOf(new Candidate(mock), new Candidate(payloadTracker)), checknames),
                checkName, bugId, 0);
    }

    @Test
    public void noViolationIfNotBelongToAPayloadTracker() {
        Mockito.when(mock.getBlocks()).thenReturn(Collections.emptyList());
        assertResultsIsAsExpected(
                engine.runChecksOnBugs(CollectionUtils.asSetOf(new Candidate(mock), new Candidate(payloadTracker)), checknames),
                checkName, bugId, 0);
    }

    @Test
    public void noViolationIfUpgradeHasAFix() {
        final String fixId = "15436473";
        final URL fixURL = MockUtils.buildURL(fixId);
        final Issue fix = MockUtils.mockBzIssue(fixId, fixURL, "bug fix");
        Mockito.when(fix.getType()).thenReturn(IssueType.BUG);
        Mockito.when(mock.getDependsOn()).thenReturn(CollectionUtils.asListOf(fixURL, MockUtils.buildURL(payloadId)));
        assertResultsIsAsExpected(engine.runChecksOnBugs(
                CollectionUtils.asSetOf(new Candidate(mock), new Candidate(payloadTracker), new Candidate(fix)), checknames),
                checkName, bugId, 0);
    }
}
