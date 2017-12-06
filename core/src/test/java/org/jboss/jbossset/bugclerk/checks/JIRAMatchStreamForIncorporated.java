package org.jboss.jbossset.bugclerk.checks;

import static org.jboss.jbossset.bugclerk.checks.utils.AssertsHelper.assertNoViolationFound;
import static org.jboss.jbossset.bugclerk.checks.utils.AssertsHelper.assertResultsIsAsExpected;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.jboss.jbossset.bugclerk.AbstractCheckRunner;
import org.jboss.jbossset.bugclerk.Candidate;
import org.jboss.jbossset.bugclerk.MockUtils;
import org.jboss.jbossset.bugclerk.checks.utils.CollectionUtils;
import org.jboss.set.aphrodite.domain.FlagStatus;
import org.jboss.set.aphrodite.domain.Issue;
import org.jboss.set.aphrodite.domain.IssueType;
import org.jboss.set.aphrodite.issue.trackers.jira.JiraIssue;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class JIRAMatchStreamForIncorporated extends AbstractCheckRunner {
    private static final String RULE = "JIRAMatchStreamForIncorporated";
    private final String mainId = "143794";
    private final String issueBadId1 = "143795";
    private final String issueBadId2 = "1437955";
    private final String issueGoodId = "143796";
    private final URL mainURL = MockUtils.buildURL(mainId);
    private final URL issueBad1URL = MockUtils.buildURL(issueBadId1);
    private final URL issueBad2URL = MockUtils.buildURL(issueBadId2);
    private final URL issueGoodURL = MockUtils.buildURL(issueGoodId);
    private Map<String, FlagStatus> goodStatus = new HashMap<>();
    private Map<String, FlagStatus> badStatus1 = new HashMap<>();
    private Map<String, FlagStatus> badStatus2 = new HashMap<>();
    private JiraIssue mainIssue;
    private JiraIssue blockingIssue_Good;
    private JiraIssue blockingIssue_Bad1;
    private JiraIssue blockingIssue_Bad2;

    @Before
    public void prepareBugMock() {

        goodStatus.put("7.1.0.GA", FlagStatus.ACCEPTED);
        badStatus1.put("7.1.0.GA", FlagStatus.REJECTED);
        badStatus2.put("7.1.0.GA", FlagStatus.ACCEPTED);
        badStatus2.put("7.2.0.GA", FlagStatus.SET);
        mainIssue = MockUtils.mockJira(mainId, mainURL, "Possibly upgrade issue");
        Mockito.when(mainIssue.getType()).thenReturn(IssueType.UPGRADE);
        Mockito.when(mainIssue.getStreamStatus()).thenReturn(goodStatus);

        blockingIssue_Bad1 = MockUtils.mockJira(issueBadId1, issueBad1URL, "Bad Issue 1");
        Mockito.when(blockingIssue_Bad1.getStreamStatus()).thenReturn(badStatus1);

        blockingIssue_Bad2 = MockUtils.mockJira(issueBadId2, issueBad2URL, "Bad Issue 2");
        Mockito.when(blockingIssue_Bad2.getStreamStatus()).thenReturn(badStatus2);

        blockingIssue_Good = MockUtils.mockJira(issueGoodId, issueGoodURL, "Good issue");
        Mockito.when(blockingIssue_Good.getStreamStatus()).thenReturn(goodStatus);
    }

    @Test
    public void bzWithNoBlocks() {
        // general check, other tests have it, but lets be thorough
        final String checkName = super.checkName + "_NoBlocks";
        final String issueId = "143816514096871";

        Issue mock = MockUtils.mockJira(issueId, "BZ issue with no blocks");
        assertNoViolationFound(engine.runCheckOnBugs(CollectionUtils.asSetOf(new Candidate(mock)), checkName), checkName,
                issueId);
    }

    @Test
    public void bzWithGoodBlocks() {
        Mockito.when(mainIssue.getLinkedIncorporatesIssues()).thenReturn(CollectionUtils.asListOf(issueGoodURL));
        assertResultsIsAsExpected(
                engine.runChecksOnBugs(CollectionUtils.asSetOf(new Candidate(mainIssue), new Candidate(blockingIssue_Good)),
                        CollectionUtils.asListOf(RULE)),
                checkName, issueBadId1, 0);
    }

    @Test
    public void bzWithBadBlocks1() {
        Mockito.when(mainIssue.getLinkedIncorporatesIssues()).thenReturn(CollectionUtils.asListOf(issueBad1URL));
        assertResultsIsAsExpected(
                engine.runChecksOnBugs(CollectionUtils.asSetOf(new Candidate(mainIssue), new Candidate(blockingIssue_Bad1)),
                        CollectionUtils.asListOf(RULE)),
                checkName, issueBadId1, 1);
    }

    @Test
    public void bzWithBadBlocks2() {
        Mockito.when(mainIssue.getLinkedIncorporatesIssues()).thenReturn(CollectionUtils.asListOf(issueBad2URL));
        assertResultsIsAsExpected(
                engine.runChecksOnBugs(CollectionUtils.asSetOf(new Candidate(mainIssue), new Candidate(blockingIssue_Bad2)),
                        CollectionUtils.asListOf(RULE)),
                checkName, issueBadId2, 1);
    }
}
