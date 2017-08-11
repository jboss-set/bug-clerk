package org.jboss.jbossset.bugclerk.aphrodite.callables;

import java.util.Map;

import org.jboss.set.aphrodite.Aphrodite;
import org.jboss.set.aphrodite.domain.Comment;
import org.jboss.set.aphrodite.domain.Issue;

public class AddCommentTask extends AphroditeCallable<Boolean> {

    private Map<Issue, Comment> comments;

    public AddCommentTask(Aphrodite aphrodite, Map<Issue, Comment> comments) {
        super(aphrodite);
        this.comments = comments;
    }

    @Override
    public Boolean call() throws Exception {
        aphrodite.addCommentToIssue(comments);
        return true;
    }
}
