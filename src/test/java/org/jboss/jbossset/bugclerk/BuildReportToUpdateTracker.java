package org.jboss.jbossset.bugclerk;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.jbossset.bugclerk.Level;
import org.jboss.jbossset.bugclerk.Violation;
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

    private Map<Issue, List<Violation>> mockLoadedResults = new HashMap<Issue, List<Violation>>(1);
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
        
        mock = MockUtils.mockBug(bugId, checkname);
        Mockito.when(mock.getComments()).thenReturn(mockComments);
                
        mockLoadedResults = new HashMap<Issue, List<Violation>>(1);
        mockLoadedResults.put(mock, violations);        
    }
    
    @Test
    public void ignoreWarningLevelChecks() {    
        Map<Issue, Comment> map = new ViolationsReportAsCommentBuilder().reportViolationToBugTracker(mockLoadedResults);
        assertTrue(map.isEmpty());
    }
    
    @Test
    public void reportIfErrorLevelChecks() {
        Mockito.when(mockLoadedResults.get(mock).get(0).getLevel()).thenReturn(Level.ERROR);              
        Map<Issue, Comment> map = new ViolationsReportAsCommentBuilder().reportViolationToBugTracker(mockLoadedResults);
        assertTrue(! map.isEmpty());
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
