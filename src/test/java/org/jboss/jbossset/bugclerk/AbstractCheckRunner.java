package org.jboss.jbossset.bugclerk;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.SortedSet;
import java.util.TreeSet;

import org.jboss.pull.shared.connectors.bugzilla.Bug;
import org.jboss.pull.shared.connectors.bugzilla.Comment;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public abstract class AbstractCheckRunner {

    protected RuleEngine engine;
    protected final String checkName;

    public AbstractCheckRunner() {
        checkName = this.getClass().getSimpleName();
    }

    @Before
    public void initRuleEngine() {
        this.engine = new RuleEngine(BugClerk.KIE_SESSION);
    }

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @After
    public void shutdownRuleEngine() {
        this.engine.shutdownRuleEngine();
    }

    protected Bug createMockedBug(int bugId) {
        Bug mock = Mockito.mock(Bug.class);
        Mockito.when(mock.getId()).thenReturn(bugId);
        Mockito.when(mock.getSummary()).thenReturn("summary");
        return testSpecificStubbingForBug(mock);
    }

    protected Bug testSpecificStubbingForBug(Bug bug) {
        return bug;
    }

    protected Comment testSpecificStubbingForComment(Comment comment) {
        return comment;
    }

    protected Comment createMockedComment(int id, String text, int bugId) {
        Comment mock = Mockito.mock(Comment.class);
        Mockito.when(mock.getId()).thenReturn(id);
        Mockito.when(mock.getBugId()).thenReturn(bugId);
        Mockito.when(mock.getText()).thenReturn(text);
        return testSpecificStubbingForComment(mock);
    }

    protected Collection<Candidate> buildTestSubject(int bugId, String payload) {
        SortedSet<Comment> comments = new TreeSet<Comment>();
        comments.add(createMockedComment(0, payload, bugId));
        final Candidate candidate = new Candidate(createMockedBug(bugId), comments);

        Collection<Candidate> candidates = new ArrayList<Candidate>(1);
        candidates.add(candidate);
        return candidates;
    }

    protected Collection<Candidate> filterCandidateOut(Collection<Candidate> candidates) {
        for ( Candidate candidate : candidates ) {
            candidate.setFiltered(true);
        }
        return candidates;
    }

    @Test
    public void filteredCandidateShouldBeIgnored() {
        final String payload = "Well; it does seems like one forgot the PR here.";
        final int bugId = 143794;

        assertThat( engine.runCheckOnBugs(checkName, filterCandidateOut(buildTestSubject(bugId, payload))).size(), is(0) );
    }

}
