package org.jboss.jbossset.bugclerk.bugzilla;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;

import org.jboss.jbossset.bugclerk.MockUtils;
import org.jboss.pull.shared.connectors.bugzilla.Comment;
import org.junit.Test;

public class CommentPatternMatcherTest {

    @Test
    public void missingCheckIsFalse() {
        CommentPatternMatcher matcher = new CommentPatternMatcher("[TargetRelease]");
        Collection<Comment> comments = new ArrayList<Comment>(3);
        comments.add(MockUtils.mockComment(0,"Not having target release", 1));
        comments.add(MockUtils.mockComment(1,"Not having target release", 1));
        comments.add(MockUtils.mockComment(2,"Not having target release", 1));
        assertFalse(matcher.containsPattern(comments));
    }

    @Test
    public void presentCheckIsTrue() {
        final String checkName = "[ReleaseVersionMismatch]";

        CommentPatternMatcher matcher = new CommentPatternMatcher(checkName);
        Collection<Comment> comments = new ArrayList<Comment>(3);
        comments.add(MockUtils.mockComment(0,"Not having target release", 1));
        comments.add(MockUtils.mockComment(1,"Having" + checkName, 1));
        assertTrue(matcher.containsPattern(comments));
    }
}
