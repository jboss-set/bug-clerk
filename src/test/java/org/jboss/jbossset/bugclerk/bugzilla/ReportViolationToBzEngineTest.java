package org.jboss.jbossset.bugclerk.bugzilla;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import org.jboss.jbossset.bugclerk.MockUtils;
import org.jboss.jbossset.bugclerk.Violation;
import org.jboss.jbossset.bugclerk.utils.CollectionUtils;
import org.jboss.pull.shared.connectors.bugzilla.Comment;
import org.junit.Test;
import org.mockito.Mockito;

public class ReportViolationToBzEngineTest {

    @Test
    public void test() {
        final int bugId = 1;
        final String bugIdAsString = String.valueOf(bugId);
        final String text = "text";
        final String checkname = "checkname";

        Collection<String> bugIds = new ArrayList<String>(1);
        bugIds.add(bugIdAsString);
        BugzillaClient client = Mockito.mock(BugzillaClient.class);
        SortedSet<Comment> mockComments = MockUtils.mockCommentsWithOneItem(1, text, bugId);

        Map<String, SortedSet<Comment>> mockLoadedResults = new HashMap<String, SortedSet<Comment>>(1);
        mockLoadedResults.put(bugIdAsString, mockComments);

        Mockito.when(client.loadCommentForBug(bugIds)).thenReturn(mockLoadedResults);
        Mockito.when(client.addPrivateCommentTo(bugId, text)).thenReturn(true);

        List<Violation> violations = MockUtils.mockViolationsListWithOneItem(bugId, checkname);

        new ReportViolationToBzEngine("header","footer", client).reportViolationToBugTracker(CollectionUtils.indexedViolationsByBugId(violations), indexedCommentByBugId(bugId, mockComments));

    }

    private Map<String, SortedSet<Comment>> indexedCommentByBugId(final int bugId, SortedSet<Comment> mockComments) {
        Map<String, SortedSet<Comment>> map = new HashMap<String, SortedSet<Comment>>();
        map.put(String.valueOf(bugId), mockComments);
        return map;
    }

}
