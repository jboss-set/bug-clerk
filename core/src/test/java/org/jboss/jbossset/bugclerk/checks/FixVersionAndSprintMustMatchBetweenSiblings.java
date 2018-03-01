/*
q * JBoss, Home of Professional Open Source.
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
import java.util.List;

import org.jboss.jbossset.bugclerk.AbstractCheckRunner;
import org.jboss.jbossset.bugclerk.Candidate;
import org.jboss.jbossset.bugclerk.MockUtils;
import org.jboss.jbossset.bugclerk.checks.utils.CollectionUtils;
import org.jboss.set.aphrodite.domain.Issue;
import org.jboss.set.aphrodite.domain.Release;
import org.jboss.set.aphrodite.issue.trackers.jira.JiraIssue;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

public class FixVersionAndSprintMustMatchBetweenSiblings extends AbstractCheckRunner {

    @Test
    public void fixVersionAndSprintNotMatchingBetweenSiblings() {
        final String checkName = super.checkName;
        final String siblingId = "JBEAP-111";
        final String parentId = "JBEAP-666";
        final String sprintId = "SPRINT-ID";

        Release sevenOhOne = new Release("7.0.1"); // Parent issue fix version
        Release sevenOhTwo = new Release("7.0.2"); // Sibling issue fix version
        
        JiraIssue sibling = MockUtils.mockJiraIssue(siblingId,"Summary");
        Mockito.when(sibling.getSprintRelease()).thenReturn(sprintId);
        Mockito.when(sibling.getReleases()).thenReturn(CollectionUtils.asListOf(sevenOhTwo));
    
        JiraIssue parentIssue = MockUtils.mockJiraIssue(parentId, "A simple BZ issue...");
        Mockito.when(parentIssue.getSprintRelease()).thenReturn(sprintId);
        Mockito.when(parentIssue.getReleases()).thenReturn(CollectionUtils.asListOf(sevenOhOne));
        List<URL> urls = CollectionUtils.asListOf(sibling.getURL());
        Mockito.when(parentIssue.getDependsOn()).thenReturn(urls);
        urls = CollectionUtils.asListOf(parentIssue.getURL());
        Mockito.when(sibling.getBlocks()).thenReturn(urls);

        assertResultsIsAsExpected(engine.runCheckOnBugs(
                CollectionUtils.asSetOf(new Candidate(parentIssue), new Candidate(sibling)), checkName), checkName, parentId,1);
    }
    
    
    @Test
    public void issueDoesComesFromJiraAndThusShouldBeIgnored() {
        final String checkName = super.checkName;
        final String issueId = "14380";
        
        Issue mock = MockUtils.mockBzIssue(issueId, "A simple BZ issue...");

        assertResultsIsAsExpected(engine.runCheckOnBugs(
                CollectionUtils.asSetOf(new Candidate(mock)), checkName), checkName, issueId,0);        
    }
    
    @Test
    public void issueHasNoSiblingSoNoViolation() {
        final String checkName = super.checkName;
        final String issueId = "14380";
        
        JiraIssue mock = MockUtils.mockJiraIssue(issueId, "A simple BZ issue...");
        Mockito.when(mock.getDependsOn()).thenReturn(new ArrayList<URL>());
        assertResultsIsAsExpected(engine.runCheckOnBugs(
                CollectionUtils.asSetOf(new Candidate(mock)), checkName), checkName, issueId,0);
    }

    @Test
    public void issueHasOnlyBZSiblingsSoNoViolation() {
        final String checkName = super.checkName;
        final String issueId = "14380";

        Issue sibling = MockUtils.mockBzIssue(issueId,"Summary");
        JiraIssue mock = MockUtils.mockJiraIssue("JBEAP-666", "A simple BZ issue...");
        List<URL> urls = CollectionUtils.asListOf(sibling.getURL()); 
        Mockito.when(mock.getDependsOn()).thenReturn(urls);

        assertResultsIsAsExpected(engine.runCheckOnBugs(
                CollectionUtils.asSetOf(new Candidate(mock), new Candidate(sibling)), checkName), checkName, issueId,0);
    }

    
    @Test
    public void siblingsHasProperFixVersionButSprintVersionDiffers() {
        final String checkName = super.checkName;
        final String issueId = "JBEAP-111";

        Release sevenOhOne = new Release("7.0.1");
        Release sevenOhTwo = new Release("7.0.2");
        List<Release> releases = CollectionUtils.asListOf(sevenOhOne, sevenOhTwo);
        
        JiraIssue sibling = MockUtils.mockJiraIssue(issueId,"Summary");
        Mockito.when(sibling.getSprintRelease()).thenReturn("a Sprint version");
        Mockito.when(sibling.getReleases()).thenReturn(releases);
        
    
        JiraIssue mock = MockUtils.mockJiraIssue("JBEAP-666", "A simple BZ issue...");
        Mockito.when(mock.getSprintRelease()).thenReturn("a different sprint version");
        Mockito.when(mock.getReleases()).thenReturn(CollectionUtils.asListOf(sevenOhOne));

        assertResultsIsAsExpected(engine.runCheckOnBugs(
                CollectionUtils.asSetOf(new Candidate(mock), new Candidate(sibling)), checkName), checkName, issueId,0);
    }

    private static List<Release> mockReleases(String... releases) {
        List<Release> releasesList = new ArrayList<Release>(releases.length);
        for ( String release: releases )
            releasesList.add(new Release(release));            
        return releasesList;
    }
    
    @Test
    public void siblingHasProperFixVersionAndSameSprint() {
        final String checkName = super.checkName;
        final String issueId = "JBEAP-111";
        final String commonReleaseVersion = "7.0.1";
        final String commonSprintVersion = "SPRINT_701";
        
        List<Release> releases = mockReleases(commonReleaseVersion, "7.0.2");
        
        JiraIssue sibling = MockUtils.mockJiraIssue(issueId,"Summary");
        Mockito.when(sibling.getSprintRelease()).thenReturn(commonSprintVersion);
        Mockito.when(sibling.getReleases()).thenReturn(releases);
        
    
        JiraIssue mock = MockUtils.mockJiraIssue("JBEAP-666", "A simple BZ issue...");
        Mockito.when(mock.getSprintRelease()).thenReturn(commonSprintVersion);
        Mockito.when(mock.getReleases()).thenReturn(CollectionUtils.asListOf(new Release(commonReleaseVersion)));

        assertResultsIsAsExpected(engine.runCheckOnBugs(
                CollectionUtils.asSetOf(new Candidate(mock), new Candidate(sibling)), checkName), checkName, issueId,0);
        
    }
    
    @Test
    public void siblingHasNoFixVersion() {

        final String checkName = super.checkName;
        final String issueId = "JBEAP-666";
        
        JiraIssue mock = MockUtils.mockJiraIssue(issueId, "summary");

        assertResultsIsAsExpected(engine.runCheckOnBugs(
                CollectionUtils.asSetOf(new Candidate(mock)), checkName), checkName, issueId,0);
    }
   
}
