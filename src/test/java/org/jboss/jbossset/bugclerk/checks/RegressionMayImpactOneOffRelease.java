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

import static org.jboss.jbossset.bugclerk.checks.utils.AssertsHelper.assertOneViolationFound;
import static org.jboss.jbossset.bugclerk.checks.utils.AssertsHelper.assertNoViolationFound;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.jboss.jbossset.bugclerk.AbstractCheckRunner;
import org.jboss.jbossset.bugclerk.Candidate;
import org.jboss.jbossset.bugclerk.MockUtils;
import org.jboss.pull.shared.connectors.bugzilla.Bug;
import org.jboss.pull.shared.connectors.bugzilla.Comment;
import org.junit.Test;
import org.mockito.Mockito;

public class RegressionMayImpactOneOffRelease extends AbstractCheckRunner {

    private final int originalBugId = 143794;
    private final int oneOffBugId = 143798;
    
    @Test
    public void oneOffBZWithRegressionInBlocksList() {
        assertOneViolationFound(engine.runCheckOnBugs(checkName, prepareCandidates("Regression here...")), checkName, oneOffBugId );
    }
    
    @Test
    public void oneOffBZWithRegressionLowerCaseInBlocksList() {
        assertOneViolationFound(engine.runCheckOnBugs(checkName, prepareCandidates("Appears to be a regression here...")), checkName, oneOffBugId );
    }

    public void oneOffButNoRegression() {
        assertNoViolationFound(engine.runCheckOnBugs(checkName, prepareCandidates("New bug ?")), checkName, oneOffBugId );
    }

    

    protected Collection<Candidate> prepareCandidates(String regressionText) {
        Collection<Candidate> candidates = new HashSet<Candidate>(2);
        candidates.add(prepareOneOff());
        candidates.add(prepareOriginal(regressionText));
        return candidates;
    }
    
    protected Candidate prepareOneOff() {
        final Bug oneOff = MockUtils.mockBug(oneOffBugId, "it's a one-off");
        Mockito.when(oneOff.getType()).thenReturn("Support Patch");
        Set<Integer> blocks = new HashSet<Integer>(1);
        blocks.add(originalBugId);
        Mockito.when(oneOff.getBlocks()).thenReturn(blocks);       
        return new Candidate(oneOff, new TreeSet<Comment>());
    }
    
    protected Candidate prepareOriginal(String regressionText) {  
        final Bug originalOne = prepareOriginalBug();
        return new Candidate(originalOne,MockUtils.mockCommentsWithOneItem(1, regressionText, originalBugId));
    }
    
    protected Bug prepareOriginalBug() {
        final Bug originalOne = MockUtils.mockBug(originalBugId, "it's the original one");
        Set<Integer> dependsOn = new HashSet<Integer>(1);
        dependsOn.add(oneOffBugId);
        Mockito.when(originalOne.getDependsOn()).thenReturn(dependsOn);      
        assert originalOne.getDependsOn().contains(oneOffBugId);
        Mockito.when(originalOne.getType()).thenReturn("Bug");
        return originalOne;
    }
    
}