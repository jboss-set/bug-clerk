package org.jboss.jbossset.bugclerk;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.jboss.pull.shared.connectors.bugzilla.Bug;
import org.jboss.pull.shared.connectors.bugzilla.Comment;
import org.mockito.Mockito;

public final class MockUtils {

    private MockUtils() {
    }

    public static Comment mockComment(int id, String text, int bugId) {
        Comment mock = Mockito.mock(Comment.class);
        Mockito.when(mock.getId()).thenReturn(id);
        Mockito.when(mock.getBugId()).thenReturn(bugId);
        Mockito.when(mock.getText()).thenReturn(text);
        return mock;
    }

    public static SortedSet<Comment> mockCommentsWithOneItem(int id, String text, int bugId) {
        SortedSet<Comment> comments = new TreeSet<Comment>();
        comments.add(mockComment(id, text, bugId));
        return comments;
    }

    public static Bug mockBug(int bugId, String summary) {
        Bug mock = Mockito.mock(Bug.class);
        Mockito.when(mock.getId()).thenReturn(bugId);
        Mockito.when(mock.getSummary()).thenReturn(summary);
        return mock;
    }

    public static Violation mockViolation(final int bugId, final String checkname) {

        Bug bug = MockUtils.mockBug(bugId, "summary");

        Violation mock = Mockito.mock(Violation.class);
        Mockito.when(mock.getBug()).thenReturn(bug);
        Mockito.when(mock.getCheckName()).thenReturn(checkname);
        Mockito.when(mock.getMessage()).thenReturn(checkname);

        return mock;
    }

    public static List<Violation> mockViolationsListWithOneItem(final int bugId, final String checkname) {
        List<Violation> violations = new ArrayList<Violation>(1);
        violations.add(mockViolation(bugId, checkname));
        return violations;
    }
}
