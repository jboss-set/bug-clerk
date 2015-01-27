package org.jboss.jbossset.bugclerk;

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

import org.jboss.pull.shared.connectors.bugzilla.Bug;
import org.jboss.pull.shared.connectors.bugzilla.Comment;

public class ParallelLoader {

    @SuppressWarnings("unchecked")
    public List<Candidate> loadCandidates(List<String> ids) {
        List<Candidate> candidates = new ArrayList<Candidate>(ids.size());

        ExecutorService executor = Executors.newFixedThreadPool(2);
        Future<Map<String, SortedSet<Comment>>> loadingComments = executor.submit(new CommentLoader(ids));
        Future<Map<String, Bug>> loadingBugs = executor.submit(new BugLoader(ids));

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
        public AbstractIssueLoader(final List<String> ids) {
            this.ids = ids;
        }
    }

    private class CommentLoader extends AbstractIssueLoader<SortedSet<Comment>> {

        public CommentLoader(List<String> ids) {
            super(ids);
        }

        @Override
        public Map<String, SortedSet<Comment>> call() throws Exception {
            return BzUtils.loadCommentForBug(ids);
        }
    }

    private class BugLoader extends AbstractIssueLoader<Bug> {

        public BugLoader(List<String> ids) {
            super(ids);
        }

        @Override
        public Map<String, Bug> call() throws Exception {
            return BzUtils.loadBugsById(new HashSet<String>(ids));
        }
    }
}
