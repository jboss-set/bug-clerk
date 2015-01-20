package org.jboss.jbossset.bugclerk;

import java.util.Collection;
import java.util.List;

import org.jboss.pull.shared.connectors.bugzilla.Bug;
import org.jboss.pull.shared.connectors.bugzilla.Bug.Status;
import org.jboss.pull.shared.connectors.bugzilla.Comment;

public class BugReport {

    private Bug bug;
    private Collection<Comment> comments;
    private List<Violation> violations;

    public Status getStatus() {
        return Bug.Status.valueOf(bug.getStatus());
    }

    public Bug getBug() {
        return bug;
    }

    public void setBug(Bug bug) {
        this.bug = bug;
    }

    public Collection<Comment> getComments() {
        return comments;
    }

    public void setComments(Collection<Comment> comments) {
        this.comments = comments;
    }

    @Override
    public String toString() {
        return "BugReport [bug=" + bug + ", comments=" + comments + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((bug == null) ? 0 : bug.hashCode());
        result = prime * result + ((comments == null) ? 0 : comments.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        BugReport other = (BugReport) obj;
        if (bug == null) {
            if (other.bug != null)
                return false;
        } else if (!bug.equals(other.bug))
            return false;
        if (comments == null) {
            if (other.comments != null)
                return false;
        } else if (!comments.equals(other.comments))
            return false;
        return true;
    }

}
