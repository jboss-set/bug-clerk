package org.jboss.jbossset.bugclerk;

import org.jboss.pull.shared.connectors.bugzilla.Comment;
import org.mockito.Mockito;

public final class MockUtils {

    private MockUtils() {}

    public static Comment mockComment(int id, String text, int bugId) {
        Comment mock = Mockito.mock(Comment.class);
        Mockito.when(mock.getId()).thenReturn(id);
        Mockito.when(mock.getBugId()).thenReturn(bugId);
        Mockito.when(mock.getText()).thenReturn(text);
        return mock;
    }
}
