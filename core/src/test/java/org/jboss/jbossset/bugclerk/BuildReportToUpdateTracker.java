package org.jboss.jbossset.bugclerk;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.jboss.jbossset.bugclerk.checks.utils.CollectionUtils;
import org.jboss.jbossset.bugclerk.comments.ViolationsReportAsCommentBuilder;
import org.jboss.set.aphrodite.domain.Comment;
import org.jboss.set.aphrodite.domain.Issue;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class BuildReportToUpdateTracker {

    final String bugId = "1";

    final String text = "text";
    final String checkname = "checkname";

    private Collection<Candidate> mockLoadedResults = new ArrayList<Candidate>(1);
    private Issue mock;

    @After
    public void emptyMocks() {
        mock = null;
        mockLoadedResults.clear();
    }

    @Before
    public void prepareMocks() {
        List<Comment> mockComments = MockUtils.mockCommentsWithOneItem("1", text, bugId);
        List<Violation> violations = MockUtils.mockViolationsListWithOneItem(bugId, checkname);

        mock = MockUtils.mockBzIssue(bugId, checkname);
        Mockito.when(mock.getComments()).thenReturn(mockComments);

        mockLoadedResults = new ArrayList<Candidate>(1);
        Candidate candidate = new Candidate(mock);
        for ( Violation v : violations )
            candidate.addViolation(v);
        mockLoadedResults.add(candidate);
    }

    @Test
    public void ignoreWarningLevelChecks() {
        Map<Issue, Comment> map = new ViolationsReportAsCommentBuilder().reportViolationToBugTracker(mockLoadedResults);
        assertTrue(map.isEmpty());
    }

    @Test
    public void reportIfErrorLevelChecks() {
        Violation v = MockUtils.mockViolation(bugId, checkname);        
        Mockito.when(v.getLevel()).thenReturn(Severity.MAJOR);
        Candidate candidate = new Candidate(mock);
        candidate.addViolation(v);
        Map<Issue, Comment> map = new ViolationsReportAsCommentBuilder().reportViolationToBugTracker(CollectionUtils.asListOf(candidate));
        assertTrue(!map.isEmpty());
        assertTrue(map.size() == 1);
        assertTrue(map.get(mock).getBody().contains(checkname));
    }

    @Test
    public void noReportIfChecknameAlreadyPresentInCommentsList() {
        List<Comment> mockComments = MockUtils.mockCommentsWithOneItem("1", checkname, bugId);
        Mockito.when(mock.getComments()).thenReturn(mockComments);
        Map<Issue, Comment> map = new ViolationsReportAsCommentBuilder().reportViolationToBugTracker(mockLoadedResults);
        assertTrue(map.isEmpty());
    }
}
