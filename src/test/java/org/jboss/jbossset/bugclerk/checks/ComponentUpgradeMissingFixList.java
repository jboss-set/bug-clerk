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

import java.util.ArrayList;
import java.util.List;

import org.jboss.jbossset.bugclerk.AbstractCheckRunner;
import org.jboss.pull.shared.connectors.bugzilla.Bug;
import org.jboss.pull.shared.connectors.common.Flag;
import org.junit.Test;
import org.mockito.Mockito;

public class ComponentUpgradeMissingFixList extends AbstractCheckRunner {

    @Test
    public void violationIfNoDependsOnAndComponentUpgradeType() {
        final String payload = "Well; it does seems like one forgot the PR here.";
        final int bugId = 143794;
        assertResultsIsAsExpected(engine.runCheckOnBugs(checkName, buildTestSubjectWithComment(bugId, payload)),checkName,bugId);
    }

    private final String TYPE = "Component Upgrade";

    @Override
    protected Bug testSpecificStubbingForBug(Bug mock) {
        Mockito.when(mock.getType()).thenReturn(TYPE);

        List<Flag> flags = new ArrayList<Flag>(1);
        Flag flag = new Flag("jboss-eap-6.4.0", "setter?", Flag.Status.POSITIVE);
        flags.add(flag);

        Mockito.when(mock.getFlags()).thenReturn(flags);
        return mock;
    }

}
