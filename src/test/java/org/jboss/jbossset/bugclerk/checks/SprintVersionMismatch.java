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
import static org.junit.Assert.assertTrue;

import java.util.Collections;

import org.jboss.jbossset.bugclerk.AbstractCheckRunner;
import org.jboss.jbossset.bugclerk.Candidate;
import org.jboss.jbossset.bugclerk.MockUtils;
import org.jboss.jbossset.bugclerk.checks.utils.CollectionUtils;
import org.jboss.set.aphrodite.issue.trackers.jira.JiraIssue;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class SprintVersionMismatch extends AbstractCheckRunner {

    private static final String SPRINT_RELEASE = "EAP 7.0.2";
    private String summary;
    private String bugId;

    @Before
    public void resetMockData() {
        bugId = "143794";
        summary = "A Summary...";
    }
    
    @Test
    public void violationIfNoReleases() {
        JiraIssue mock = MockUtils.mockBug(MockUtils.buildURL(bugId), summary, JiraIssue.class);
        Mockito.when(mock.getSprintRelease()).thenReturn(SPRINT_RELEASE);
        Mockito.when(mock.getReleases()).thenReturn(Collections.emptyList());
        assertTrue(mock.getReleases().isEmpty());
        assertThat(engine.runCheckOnBugs(CollectionUtils.asSetOf(new Candidate(mock)), checkName + "_NoRelease").size(), is(1));
    }
    
    @Test
    public void noViolationIfNoSprint() {
        JiraIssue mock = MockUtils.mockBug(MockUtils.buildURL(bugId), summary, JiraIssue.class);
        Mockito.when(mock.getReleases()).thenReturn(Collections.emptyList());
        Mockito.when(mock.getSprintRelease()).thenReturn("");
        assertThat(engine.runCheckOnBugs(CollectionUtils.asSetOf(new Candidate(mock)), checkName).size(), is(0));
    }

    
    @Test
    public void noViolationIfOneReleasesMatchesSprintVersion() {
        JiraIssue mock = MockUtils.mockBug(MockUtils.buildURL(bugId), summary, JiraIssue.class);
        Mockito.when(mock.getSprintRelease()).thenReturn(SPRINT_RELEASE);
        Mockito.when(mock.getReleases()).thenReturn(MockUtils.mockReleases("7.0.2", "7.0.3"));
        assertThat(engine.runCheckOnBugs(CollectionUtils.asSetOf(new Candidate(mock)), checkName).size(), is(0));
    }
    
    @Test
    public void violationIfOneReleasesMatchesSprintVersion() {
        JiraIssue mock = MockUtils.mockBug(MockUtils.buildURL(bugId), summary, JiraIssue.class);
        Mockito.when(mock.getSprintRelease()).thenReturn("EAP 7.1");
        Mockito.when(mock.getReleases()).thenReturn(MockUtils.mockReleases("7.0.2", "7.0.3"));
        assertThat(engine.runCheckOnBugs(CollectionUtils.asSetOf(new Candidate(mock)), checkName).size(), is(1));
    }
    

}