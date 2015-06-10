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
import static org.jboss.jbossset.bugclerk.checks.utils.CollectionUtils.asSetOf;
import static org.junit.Assert.assertTrue;

import java.util.TreeSet;

import org.jboss.jbossset.bugclerk.AbstractCheckRunner;
import org.jboss.jbossset.bugclerk.Candidate;
import org.jboss.jbossset.bugclerk.MockUtils;
import org.jboss.jbossset.bugclerk.checks.utils.CollectionUtils;
import org.jboss.pull.shared.connectors.bugzilla.Bug;
import org.jboss.pull.shared.connectors.bugzilla.Comment;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class OneReleaseMustOfPatchType extends AbstractCheckRunner {

    private Bug mock;

    @Before
    public void testSpecificStubbingForBug() {
        mock = MockUtils.mockBug(143794, "summary");
        Mockito.when(mock.getType()).thenReturn("Bug");
        Mockito.when(mock.getTargetRelease()).thenReturn(asSetOf("One-off"));
    }

    @Test
    public void simpleTestCase() {
        final int bugId = 143794;
        assertResultsIsAsExpected( engine.runCheckOnBugs(checkName, buildTestSubjectWithComment(mock, "comment")), checkName, bugId );
    }

    @Test
    public void falsePositive() {
        Bug mock = MockUtils.mockBug(143794, "summary");
        Mockito.when(mock.getType()).thenReturn("Support Patch");
        Mockito.when(mock.getTargetRelease()).thenReturn(asSetOf("One-off"));

        assertTrue(engine.runCheckOnBugs(checkName, CollectionUtils.asSetOf(new Candidate(mock, new TreeSet<Comment>()))).isEmpty());
    }
}
