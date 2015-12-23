package org.jboss.jbossset.bugclerk.bugzilla;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;

import org.jboss.jbossset.bugclerk.MockUtils;
import org.jboss.jbossset.bugclerk.comments.CommentPatternMatcher;
import org.jboss.set.aphrodite.domain.Comment;
import org.junit.Test;

public class CommentPatternMatcherTest {

    private final static String parentIssueId = "1";

    @Test
    public void missingCheckIsFalse() {

        CommentPatternMatcher matcher = new CommentPatternMatcher("[TargetRelease]");
        Collection<Comment> comments = new ArrayList<Comment>(3);
        for (int idComment = 0; idComment < 3; idComment++)
            comments.add(MockUtils.mockComment("" + idComment, "Not having a target release", parentIssueId));
        assertFalse(matcher.containsPattern(comments));
    }

    @Test
    public void presentCheckIsTrue() {
        final String checkName = "[ReleaseVersionMismatch]";

        CommentPatternMatcher matcher = new CommentPatternMatcher(checkName);
        Collection<Comment> comments = new ArrayList<Comment>(3);
        comments.add(MockUtils.mockComment("0", "Not having target release", parentIssueId));
        comments.add(MockUtils.mockComment("1", "Having" + checkName, parentIssueId));
        assertTrue(matcher.containsPattern(comments));
    }
}
