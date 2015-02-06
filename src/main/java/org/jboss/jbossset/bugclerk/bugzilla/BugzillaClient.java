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
package org.jboss.jbossset.bugclerk.bugzilla;

import java.net.URL;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.jbossset.bugclerk.utils.LoggingUtils;
import org.jboss.pull.shared.connectors.bugzilla.BZHelper;
import org.jboss.pull.shared.connectors.bugzilla.Bug;
import org.jboss.pull.shared.connectors.bugzilla.Comment;
import org.jboss.pull.shared.connectors.bugzilla.CommentVisibility;
import org.jboss.pull.shared.connectors.common.Flag;

public class BugzillaClient {

    private final BZHelper bugzillaHelper;

    public BugzillaClient() {
        bugzillaHelper = createHelper();
    }

    public static final String CONFIGURATION_FILENAME = "bugclerk.properties";
    private static final Logger LOGGER = LoggingUtils.getLogger();

    private static BZHelper createHelper() {
        try {
            return new BZHelper(CONFIGURATION_FILENAME, CONFIGURATION_FILENAME);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public Bug loadBzFromUrl(URL url) {
        return logBugRetrieved((Bug) this.bugzillaHelper.findIssue(url));
    }

    private static Bug logBugRetrieved(Bug issue) {
        if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.fine("BZ" + issue.getNumber());
            LOGGER.fine("Status:" + issue.getStatus());
            LOGGER.fine("Summary:" + issue.getSummary());
            for (Flag flag : issue.getFlags())
                LOGGER.fine("Flag:" + flag.getName());
        }
        return issue;
    }

    private static SortedSet<Comment> logRetrievedComments(SortedSet<Comment> comments) {
        if (LOGGER.isLoggable(Level.FINE)) {
            for (Comment comment : comments) {
                LOGGER.fine("Comment ID:" + comment.getId());
                LOGGER.fine("Comment Text:" + comment.getText());
            }
        }
        return comments;
    }

    public SortedSet<Comment> loadCommentForBug(Bug bug) {
        return logRetrievedComments(this.bugzillaHelper.loadCommentsFor(bug));
    }

    public Map<String, SortedSet<Comment>> loadCommentForBug(Collection<String> bugIds) {
        return this.bugzillaHelper.loadCommentsFor(bugIds);
    }

    public Map<String, Bug> loadBugsById(Set<String> bugIds) {
        return this.bugzillaHelper.loadIssues(bugIds);
    }

    public boolean addPrivateCommentTo(final int id, final String text) {
        return this.bugzillaHelper.addComment(id, text, CommentVisibility.PRIVATE, 0);
    }
}