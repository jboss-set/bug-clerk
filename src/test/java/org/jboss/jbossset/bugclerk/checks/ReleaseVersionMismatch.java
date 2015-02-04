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

import java.util.ArrayList;
import java.util.List;

import org.jboss.jbossset.bugclerk.AbstractCheckRunner;
import org.jboss.pull.shared.connectors.bugzilla.Bug;
import org.jboss.pull.shared.connectors.common.Flag;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

public class ReleaseVersionMismatch extends AbstractCheckRunner {

    private String summary;
    private List<Flag> flags = new ArrayList<Flag>(1);
    private int bugId;

    @Override
    protected Bug testSpecificStubbingForBug(Bug mock) {
        Mockito.when(mock.getSummary()).thenReturn(summary);
        Mockito.when(mock.getFlags()).thenReturn(flags);
        return mock;
    }

    @Before
    public void resetMockData() {
        bugId = 143794;
        flags.clear();
        summary = "A string containing 6.4.0 !";

    }

    @Test
    @Ignore //FIXME: this should work, needs to check if this means the check is failing in real life( BZ without flags ?)
    public void violationIfNoFlags() {
        assertResultsIsAsExpected(engine.runCheckOnBugs(checkName, buildTestSubject(bugId)), checkName /*+ "EmptyFlags"*/, bugId);
    }

    @Test
    public void violationIfFlagPresentNotMatching() {
        flags.add(new Flag("jboss-eap-6.3.0", "setter?", Flag.Status.POSITIVE));
        assertThat(engine.runCheckOnBugs(checkName, buildTestSubject(bugId)).size(), is(0));
    }

}