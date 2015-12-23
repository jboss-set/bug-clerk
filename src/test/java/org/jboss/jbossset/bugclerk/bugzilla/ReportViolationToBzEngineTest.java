package org.jboss.jbossset.bugclerk.bugzilla;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.jbossset.bugclerk.MockUtils;
import org.jboss.jbossset.bugclerk.Violation;
import org.jboss.set.aphrodite.domain.Comment;
import org.jboss.set.aphrodite.issue.trackers.bugzilla.BugzillaClient;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

@Ignore("Needs to rework the add comment mech")
public class ReportViolationToBzEngineTest {

    @Test
    public void test() {
        final String bugId = "1";
        final String commentId = "1";

        final String bugIdAsString = String.valueOf(bugId);
        final String text = "text";
        final String checkname = "checkname";

        Collection<String> bugIds = new ArrayList<String>(1);
        bugIds.add(bugIdAsString);
        @SuppressWarnings("unused")
        BugzillaClient client = Mockito.mock(BugzillaClient.class);
        List<Comment> mockComments = MockUtils.mockCommentsWithOneItem(commentId, text, bugId);

        Map<String, List<Comment>> mockLoadedResults = new HashMap<String, List<Comment>>(1);
        mockLoadedResults.put(bugIdAsString, mockComments);

        List<Violation> violations = MockUtils.mockViolationsListWithOneItem(bugId, checkname);

        throw new UnsupportedOperationException("To be fixed");
        /*
         * new ReportViolationToBzEngine("header", "footer").reportViolationToBugTracker(
         * CollectionUtils.indexedViolationsByBugId(violations), CollectionUtils.indexedCommentByBugId(bugId, mockComments));
         */
    }
}
