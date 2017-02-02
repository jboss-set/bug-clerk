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
package org.jboss.jbossset.bugclerk;

import java.util.HashMap;
import java.util.SortedSet;
import java.util.TreeSet;

import org.jboss.set.aphrodite.domain.Comment;
import org.junit.After;
import org.junit.Before;
import org.mockito.MockitoAnnotations;

public abstract class AbstractCheckRunner {

    protected RuleEngine engine;
    protected final String checkName;

    protected static final String DEV_ACK_FLAG = "devel_ack";
    protected static final String QA_ACK_FLAG = "qa_ack";
    protected static final String PM_ACK_FLAG = "pm_ack";

    protected static final String INDEX_PAYLOAD_RULE = "IndexPayloadTrackerByURL";
    protected static final String INDEX_ISSUE_RULE = "IndexIssueByURL";

    protected static final SortedSet<Comment> NO_COMMENTS = new TreeSet<Comment>();

    public AbstractCheckRunner() {
        checkName = this.getClass().getSimpleName();
    }

    @Before
    public void initRuleEngine() {
        this.engine = new RuleEngine(new HashMap<String, Object>(0),null);
    }

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @After
    public void shutdownRuleEngine() {
        this.engine.shutdownRuleEngine();
    }
}
