package org.jboss.jbossset.bugclerk.bugzilla;

import java.util.ArrayList;
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

    @SuppressWarnings("unchecked")
    public List<Candidate> loadCandidates(List<String> ids) {
        List<Candidate> candidates = new ArrayList<Candidate>(ids.size());

        ExecutorService executor = Executors.newFixedThreadPool(2);
        Future<Map<String, SortedSet<Comment>>> loadingComments = executor.submit(new CommentLoader(ids, bugzillaClient));
        Future<Map<String, Bug>> loadingBugs = executor.submit(new BugLoader(ids, bugzillaClient));

        while (! loadingBugs.isDone() || ! loadingComments.isDone() ) ;
        Map<String,Bug> bugs = getFromFuture(loadingBugs);
        Map<String, SortedSet<Comment>> comments = getFromFuture(loadingComments);
        executor.shutdown();
        for ( Bug bug : bugs.values() )
            candidates.add(new Candidate(bug, CollectionUtils.getEntryOrEmptySet(String.valueOf(bug.getId()),comments) ));
        return candidates;
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

    private abstract class AbstractIssueLoader<T> implements Callable<Map<String, T>>{
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
