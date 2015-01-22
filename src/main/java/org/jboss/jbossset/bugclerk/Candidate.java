package org.jboss.jbossset.bugclerk;

import java.util.SortedSet;

import org.jboss.pull.shared.connectors.bugzilla.Bug;
import org.jboss.pull.shared.connectors.bugzilla.Comment;

public class Candidate {

    private final Bug bug;
    private final SortedSet<Comment> comments;

    private boolean isCandidate = true;
    private boolean filtered = false;

    private static void checkIfNotNull(Object ref, String fieldName) {
        if ( ref == null ) {
            throw new IllegalArgumentException("Can't build instance of " + Candidate.class.getCanonicalName() + " with 'null' value for field:" + fieldName);
        }
    }

    public Candidate(Bug bug, SortedSet<Comment> comments) {
        checkIfNotNull(bug, "bug");
        checkIfNotNull(comments, "comments");
        this.bug = bug;
        this.comments = comments;
    }

    public boolean isCandidate() {
        return isCandidate;
    }

    public void setCandidate(boolean isCandidate) {
        this.isCandidate = isCandidate;
    }

    public boolean isFiltered() {
        return filtered;
    }

    public void setFiltered(boolean filtered) {
        this.filtered = filtered;
    }

    public Bug getBug() {
        return bug;
    }

    public SortedSet<Comment> getComments() {
        return comments;
    }

    @Override
    public String toString() {
        return "Candidate [bug=" + bug.getId() + ", comments=" + comments.size() + ", isCandidate=" + isCandidate + ", filtered=" + filtered
                + "]";
    }

}
