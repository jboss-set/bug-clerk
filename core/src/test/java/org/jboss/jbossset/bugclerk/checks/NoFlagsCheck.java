/*
 * JBoss, Home of Professional Open Source.
 * Copyright (c) 2016, Red Hat, Inc., and individual contributors
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

import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.Optional;

import org.jboss.jbossset.bugclerk.AbstractCheckRunner;
import org.jboss.jbossset.bugclerk.Candidate;
import org.jboss.jbossset.bugclerk.MockUtils;
import org.jboss.jbossset.bugclerk.checks.utils.CollectionUtils;
import org.jboss.set.aphrodite.domain.Issue;
import org.jboss.set.aphrodite.domain.IssueStatus;
import org.jboss.set.aphrodite.domain.Stage;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 */
public class NoFlagsCheck extends AbstractCheckRunner {
    
    @Test
    public void testNoFlags() {
        final String bugId = "143794";
        final Issue mock = MockUtils.mockBzIssue(bugId, "summary");
        Mockito.when(mock.getLastUpdated()).thenReturn(Optional.of(new GregorianCalendar(2000, 0, 1).getTime()));
        Mockito.when(mock.getStatus()).thenReturn(IssueStatus.POST);
        Mockito.when(mock.getStage()).thenReturn(new Stage());
        final Collection<Candidate> violations = engine.runChecksOnBugs(CollectionUtils.asSetOf(new Candidate(mock)), Collections.emptyList());
        // as long as we have no NullPointerExceptions and some Violations, we're good.
        assertTrue(violations.size() > 0);
    }
}
