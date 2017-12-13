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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.jboss.jbossset.bugclerk.AbstractCheckRunner;
import org.jboss.jbossset.bugclerk.Candidate;
import org.jboss.jbossset.bugclerk.MockUtils;
import org.jboss.jbossset.bugclerk.aphrodite.AphroditeClient;
import org.jboss.jbossset.bugclerk.utils.URLUtils;
import org.jboss.set.aphrodite.domain.Issue;
import org.jboss.set.aphrodite.domain.IssueStatus;
import org.jboss.set.aphrodite.domain.IssueType;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class ResolvedIssueWithFixVersionShouldHaveComponentUpgrade extends AbstractCheckRunner {

    private Issue mock;
    private final String bugId = "143794";

    @Before
    public void mockIssue() {
        mock = MockUtils.mockBzIssue(bugId, "summary");
    }
    
    @Test
    public void noViolationIfResolvedWithFixVersionAndComponentUpgradeDependency() {
        Issue componentUpgradeDependency = mockPullRequestDependency();
        AphroditeClient client = mockAphroditeClientIfNeeded();
        Mockito.when(client.loadIssuesFromUrls(Mockito.any())).thenReturn(Arrays.asList(componentUpgradeDependency));
        Mockito.when(mock.getReleases()).thenReturn(MockUtils.mockReleases("7.1.0"));
        Mockito.when(mock.getStatus()).thenReturn(IssueStatus.MODIFIED);
        Mockito.when(mock.getBlocks()).thenReturn(Arrays.asList(URLUtils.createURLFromString("http://component.upgrade/")));
            
        assertResultsIsAsExpected(
                engine.runCheckOnBugs(buildCandidatesFor(mock, componentUpgradeDependency), checkName), checkName,
                bugId,0);
    }

    @Test
    public void noViolationIfNotResolved() {
        Issue componentUpgradeDependency = mockPullRequestDependency();
        AphroditeClient client = mockAphroditeClientIfNeeded();
        Mockito.when(client.loadIssuesFromUrls(Mockito.any())).thenReturn(Arrays.asList(componentUpgradeDependency));
        Mockito.when(mock.getReleases()).thenReturn(MockUtils.mockReleases("7.1.0"));
        Mockito.when(mock.getStatus()).thenReturn(IssueStatus.RELEASE_PENDING);
        Mockito.when(mock.getBlocks()).thenReturn(Arrays.asList(URLUtils.createURLFromString("http://component.upgrade/")));
            
        assertResultsIsAsExpected(
                engine.runCheckOnBugs(buildCandidatesFor(mock, componentUpgradeDependency), checkName), checkName,
                bugId,0);
    }

    
    @Test
    public void violationIfResolvedWithFixVersionAndNoComponentUpgradeDependency() {
        AphroditeClient client = mockAphroditeClientIfNeeded();
        Mockito.when(client.loadIssuesFromUrls(Mockito.any())).thenReturn(Collections.emptyList());
        Mockito.when(mock.getReleases()).thenReturn(MockUtils.mockReleases("7.1.0"));
        Mockito.when(mock.getStatus()).thenReturn(IssueStatus.MODIFIED);

            
        assertResultsIsAsExpected(
                engine.runCheckOnBugs(buildCandidatesFor(mock), checkName), checkName,
                bugId);
    }
    
    private Set<Candidate> buildCandidatesFor(Issue...issues) {
        Set<Candidate> candidates = new HashSet<Candidate>();
        for ( Issue issue: issues) candidates.add(buildCandidateFor(issue));
        return candidates;                
    }
    
    private Candidate buildCandidateFor(Issue issue) {
        Candidate candidate = new Candidate(issue);
        candidate.setFiltered(true);
        return candidate;        
    }
    
    private Issue mockPullRequestDependency() {
        Issue componentUpgrade = MockUtils.mockBzIssue("13404", "this is a component upgrade");
        Mockito.when(componentUpgrade.getType()).thenReturn(IssueType.UPGRADE);
        return componentUpgrade;
    }

}
