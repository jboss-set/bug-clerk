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

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.jboss.jbossset.bugclerk.Candidate;
import org.jboss.jbossset.bugclerk.utils.CollectionUtils;
import org.jboss.pull.shared.connectors.bugzilla.Bug;
import org.jboss.pull.shared.connectors.bugzilla.Comment;

public class ParallelLoader {

    private BugzillaClient bugzillaClient = new BugzillaClient();

    public List<Candidate> loadCandidates(List<String> ids) {

        ExecutorService executor = Executors.newFixedThreadPool(2);
        Future<Map<String, SortedSet<Comment>>> loadingComments = executor.submit(new CommentLoader(ids, bugzillaClient));
        Future<Map<String, Bug>> loadingBugs = executor.submit(new BugLoader(ids, bugzillaClient));

        Map<String, Bug> bugs = getFromFuture(loadingBugs);
        Map<String, SortedSet<Comment>> comments = getFromFuture(loadingComments);
        executor.shutdown();
        return CollectionUtils.createCandidateList(bugs, comments);
    }

    private static <T> T getFromFuture(Future<T> future) {
        try {
            return future.get();
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        } catch (ExecutionException e) {
            throw new IllegalStateException(e);
        }
    }

    private abstract class AbstractIssueLoader<T> implements Callable<Map<String, T>> {
        protected final List<String> ids;
        protected final BugzillaClient bugzillaClient;

        public AbstractIssueLoader(final List<String> ids, final BugzillaClient bugzillaClient) {
            this.ids = ids;
            this.bugzillaClient = bugzillaClient;
        }
    }

    private class CommentLoader extends AbstractIssueLoader<SortedSet<Comment>> {

        public CommentLoader(List<String> ids, BugzillaClient bugzillaClient) {
            super(ids, bugzillaClient);
        }

        @Override
        public Map<String, SortedSet<Comment>> call() throws Exception {
            return bugzillaClient.loadCommentForBug(ids);
        }
    }

    private class BugLoader extends AbstractIssueLoader<Bug> {

        public BugLoader(List<String> ids, BugzillaClient bugzillaClient) {
            super(ids, bugzillaClient);
        }

        @Override
        public Map<String, Bug> call() throws Exception {
            return bugzillaClient.loadBugsById(new HashSet<String>(ids));
        }
    }
}
