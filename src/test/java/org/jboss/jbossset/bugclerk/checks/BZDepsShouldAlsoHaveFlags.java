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

import java.util.Collection;

import org.jboss.jbossset.bugclerk.AbstractCheckRunner;
import org.jboss.jbossset.bugclerk.Candidate;
import org.jboss.jbossset.bugclerk.MockUtils;
import org.jboss.jbossset.bugclerk.checks.utils.AssertsHelper;
import org.jboss.jbossset.bugclerk.checks.utils.CollectionUtils;
import org.jboss.set.aphrodite.domain.Flag;
import org.jboss.set.aphrodite.domain.FlagStatus;
import org.jboss.set.aphrodite.domain.Issue;
import org.jboss.set.aphrodite.domain.Stage;
import org.junit.Test;
import org.mockito.Mockito;

public class BZDepsShouldAlsoHaveFlags extends AbstractCheckRunner {

    @Test
    public void bzOnModifiedAndHasDevFlag() {
        final String dependencyId = "14380";

        Issue dependency = MockUtils.mockBug(dependencyId, "dependency payload");
        Mockito.when(dependency.getStage()).thenReturn(buildStageMapForDeps());

        Issue payload = MockUtils.mockBug("143794", "payload bug");
        payload.getDependsOn().add(dependency.getURL());
        Mockito.when(payload.getStage()).thenReturn(buildStageMapForPayload());

        Collection<Candidate> mocks = CollectionUtils.buildCollectionOfCandidates(payload, dependency);
        Mockito.when(payload.getDependsOn()).thenReturn(MockUtils.idsAsURLs(dependencyId, "158690"));
        Collection<Candidate> candidates = engine.runCheckOnBugs(mocks, checkName);
        assertThat(candidates.size(), is(2));
 //       AssertsHelper.checkResults(candidates, payload.getTrackerId().get(), 1, checkName);
        AssertsHelper.assertResultsIsAsExpected(candidates, checkName, payload.getTrackerId().get(), 1);
        
    }
    
    private static Stage buildStageMapForDeps() {
        Stage stage = new Stage();
        for (Flag flag : Flag.values())
            stage.getStateMap().put(flag, FlagStatus.NO_SET);
        return stage;
    }

    private static Stage buildStageMapForPayload() {
        Stage stage = new Stage();
        for (Flag flag : Flag.values())
            stage.getStateMap().put(flag, FlagStatus.ACCEPTED);
        return stage;
    }
}
