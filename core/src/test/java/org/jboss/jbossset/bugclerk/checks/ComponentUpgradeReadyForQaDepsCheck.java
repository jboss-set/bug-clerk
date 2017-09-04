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
import static org.jboss.jbossset.bugclerk.checks.utils.AssertsHelper.assertOneViolationFound;
import static org.mockito.Matchers.any;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import org.jboss.jbossset.bugclerk.AbstractCheckRunner;
import org.jboss.jbossset.bugclerk.Candidate;
import org.jboss.jbossset.bugclerk.MockUtils;
import org.jboss.jbossset.bugclerk.aphrodite.AphroditeClient;
import org.jboss.jbossset.bugclerk.checks.utils.CollectionUtils;
import org.jboss.jbossset.bugclerk.utils.URLUtils;
import org.jboss.set.aphrodite.domain.Issue;
import org.jboss.set.aphrodite.domain.IssueStatus;
import org.jboss.set.aphrodite.domain.IssueType;
import org.junit.Test;
import org.mockito.Mockito;

public class ComponentUpgradeReadyForQaDepsCheck extends AbstractCheckRunner {

    private final IssueType TYPE = IssueType.UPGRADE;
    private AphroditeClient aphroditeClient;
    private static final String JIRA_SERVER = "https://issues.jboss.org/browse/";
    
    @Test
    public void noViolationIfNoDependsOn() {
        final String bugId = "143794";
        final Issue mock = MockUtils.mockBug(bugId, "summary");
        Mockito.when(mock.getType()).thenReturn(TYPE);
        Mockito.when(mock.getStatus()).thenReturn(IssueStatus.ON_QA);
        Mockito.when(mock.getDependsOn()).thenReturn(new ArrayList<URL>());

        assertNoViolationFound(engine.runCheckOnBugs(CollectionUtils.asSetOf(new Candidate(mock)), checkName), checkName,
                bugId);
    }
    
    @Test
    public void noViolationIfOneDependencyOnQA() {
        
        final Issue dep = MockUtils.mockBug("JBEAP-667", "dependency");
        Mockito.when(dep.getStatus()).thenReturn(IssueStatus.ON_QA);
        URL urlDep = URLUtils.createURLFromString(JIRA_SERVER + "JBEAP-666");
        Mockito.when(dep.getURL()).thenReturn(urlDep);
        final String bugId = "JBEAP-666";
        final Issue mock = MockUtils.mockBug(bugId, "summary");
        Mockito.when(mock.getType()).thenReturn(TYPE);
        Mockito.when(mock.getStatus()).thenReturn(IssueStatus.ON_QA);
        Mockito.when(mock.getDependsOn()).thenReturn(Arrays.asList(urlDep));

        assertNoViolationFound(engine.runCheckOnBugs(CollectionUtils.asSetOf(new Candidate(mock)), checkName), checkName,
                bugId);
    }

    @Override
    protected AphroditeClient mockAphroditeClientIfNeeded() {
        this.aphroditeClient = super.mockAphroditeClientIfNeeded();
        return aphroditeClient;
    }

    
    @Test
    public void violationIfOneDependencyNotOnQA() {

        final String bugId = "JBEAP-666";
        final String depId = "JBEAP-667";
        final Issue dep = MockUtils.mockBug(depId, "dependency");
        Mockito.when(dep.getStatus()).thenReturn(IssueStatus.ASSIGNED);
        URL urlDep = URLUtils.createURLFromString(JIRA_SERVER + depId);
        Mockito.when(dep.getURL()).thenReturn(urlDep);
        final Issue mock = MockUtils.mockBug(bugId, "summary");
        URL urlBug = URLUtils.createURLFromString(JIRA_SERVER + bugId);
        Mockito.when(mock.getURL()).thenReturn(urlBug);
        Mockito.when(mock.getType()).thenReturn(TYPE);
        Mockito.when(mock.getStatus()).thenReturn(IssueStatus.ON_QA);
        Mockito.when(mock.getDependsOn()).thenReturn(Arrays.asList(urlDep));
        
        Mockito.when(aphroditeClient.loadIssuesFromUrls(any())).thenReturn(Arrays.asList(dep));
        
        assertOneViolationFound(engine.runCheckOnBugs(CollectionUtils.asSetOf(new Candidate(mock)), checkName), checkName,
                bugId);
    }
}
