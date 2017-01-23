package org.jboss.jbossset.bugclerk;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.jboss.jbossset.bugclerk.checks.utils.DateUtils;
import org.jboss.jbossset.bugclerk.utils.URLUtils;
import org.jboss.set.aphrodite.config.TrackerType;
import org.jboss.set.aphrodite.domain.Comment;
import org.jboss.set.aphrodite.domain.Flag;
import org.jboss.set.aphrodite.domain.FlagStatus;
import org.jboss.set.aphrodite.domain.Issue;
import org.jboss.set.aphrodite.domain.IssueEstimation;
import org.jboss.set.aphrodite.domain.IssueStatus;
import org.jboss.set.aphrodite.domain.IssueType;
import org.jboss.set.aphrodite.domain.Release;
import org.jboss.set.aphrodite.domain.Stage;
import org.jboss.set.aphrodite.domain.User;
import org.jboss.set.aphrodite.issue.trackers.jira.JiraIssue;
import org.mockito.Mockito;

public final class MockUtils {

    private static final String TRACKER_URL_PREFIX = "https://bugzilla.redhat.com/show_bug.cgi?id=";

    private MockUtils() {
    }

    private static URL buildUrlFromIssueId(final String id) {
        try {
            return new URL(TRACKER_URL_PREFIX + id);
        } catch (MalformedURLException e) {
            throw new IllegalStateException(e);
        }
    }

    private static URL buildJiraUrlFromId(final String id) {
        try {
            return new URL(TRACKER_URL_PREFIX + id);
        } catch (MalformedURLException e) {
            throw new IllegalStateException(e);
        }

    }

    public static Comment mockComment(String id, String text, String bugId) {
        Comment mock = Mockito.mock(Comment.class);
        Mockito.when(mock.getId()).thenReturn(Optional.of(bugId));
        Mockito.when(mock.getBody()).thenReturn(text);
        return mock;
    }

    public static List<Comment> mockCommentsWithOneItem(String id, String text, String bugId) {
        List<Comment> comments = new ArrayList<Comment>();
        comments.add(mockComment(id, text, bugId));
        return comments;
    }

    public static URL buildURL(String id) {
        try {
            return new URL(TRACKER_URL_PREFIX + id);
        } catch (MalformedURLException e) {
            throw new IllegalStateException(e);
        }
    }

    public static Issue mockBug(String bugId, String summary) {
        return mockBug(bugId, buildURL(bugId), summary);
    }

    public static Issue mockBug(String bugId, URL bugURL, String summary) {
        Issue mock = populateMock(bugId, bugURL, summary, createMockStub(TrackerType.BUGZILLA));
        List<Release> releases = mockReleases("6.4.0","");
        Mockito.when(mock.getReleases()).thenReturn(releases);
        return mock;
    }

    public static JiraIssue mockJiraIssue(String bugId, String summary) {
        JiraIssue issue = (JiraIssue) createMockStub(TrackerType.JIRA);
        return populateMock(bugId, buildJiraUrlFromId(bugId), summary, issue);
    }

    public static JiraIssue mockJiraIssue(URL bugId, String summary) {
        return (JiraIssue) populateMock(URLUtils.extractJiraTrackerId(bugId),bugId, summary, createMockStub(TrackerType.JIRA));
    }

    private static Issue mockTrackerType(Issue issue, TrackerType type) {
        Mockito.when(issue.getTrackerType()).thenReturn(type);
        return issue;
    }

    private static Issue createMockStub(TrackerType type) {
        switch(type) {
            case JIRA:
                return mockTrackerType(Mockito.mock(JiraIssue.class), TrackerType.JIRA);
            case BUGZILLA:
            default:
                return mockTrackerType(Mockito.mock(Issue.class), TrackerType.BUGZILLA);
        }
    }

    public static <T extends Issue> T populateMock(String bugId, URL bugURL, String summary, T mock) {
        final Optional<IssueEstimation> estimation = Optional.of(mockEstimation(8));

        Mockito.when(mock.getTrackerId()).thenReturn(Optional.of(bugId));
        Mockito.when(mock.getURL()).thenReturn(bugURL);
        Mockito.when(mock.getSummary()).thenReturn(Optional.of(summary));
        Mockito.when(mock.getType()).thenReturn(IssueType.BUG);
        Mockito.when(mock.getEstimation()).thenReturn(estimation);
        Mockito.when(mock.getStatus()).thenReturn(IssueStatus.NEW);
        Mockito.when(mock.getStage()).thenReturn(mockStage());
        Mockito.when(mock.getLastUpdated()).thenReturn(Optional.of(DateUtils.threeWeeksAgo()));
        Mockito.when(mock.getCreationTime()).thenReturn(Optional.of(DateUtils.threeMonthAgo()));
        Mockito.when(mock.getAssignee()).thenReturn(Optional.of(User.createWithEmail("jboss-set@redhat.com")));
        Mockito.when(mock.getReporter()).thenReturn(Optional.of(User.createWithEmail("Romain Pelisse <belaran@redhat.com>")));
        return mock;
    }

    public static List<Release> mockReleases(String releaseVersion, String milestone) {
        List<Release> releases = new ArrayList<Release>(1);
        releases.add(new Release(releaseVersion,milestone));
        return releases;
    }

    private static Stage mockStage() {
        Stage stage = new Stage();
        stage.getStateMap().put(Flag.PM, FlagStatus.NO_SET);
        stage.getStateMap().put(Flag.DEV, FlagStatus.NO_SET);
        stage.getStateMap().put(Flag.QE, FlagStatus.NO_SET);
        return stage;
    }

    public static Violation mockViolation(final String bugId, final String checkname) {
        Violation mock = Mockito.mock(Violation.class);
        Mockito.when(mock.getCheckName()).thenReturn(checkname);
        Mockito.when(mock.getMessage()).thenReturn("Message for " + checkname + ".");
        Mockito.when(mock.getLevel()).thenReturn(Severity.MINOR);
        return mock;
    }

    public static List<Violation> mockViolationsListWithOneItem(final String bugId, final String checkname) {
        List<Violation> violations = new ArrayList<Violation>(1);
        violations.add(mockViolation(bugId, checkname));
        return violations;
    }

    private static IssueEstimation mockEstimation(double estimate) {
        IssueEstimation estimation = Mockito.mock(IssueEstimation.class);
        Mockito.when(estimation.getInitialEstimate()).thenReturn(estimate);
        return estimation;
    }

    public static List<URL> idsAsURLs(String... ids) {
        List<URL> urls = new ArrayList<>();
        for (String id : ids) {
            urls.add(buildUrlFromIssueId(id));
        }
        return urls;
    }

    public static  List<Issue> generateMockIssues(int nbIssue, String idPrefix, String summaryPrefix) {
       List<Issue> issues = new ArrayList<>(nbIssue);
        for ( int i = 1; i < (nbIssue + 1); i++ )
            issues.add(MockUtils.mockBug(idPrefix + i, summaryPrefix + i));
        return issues;
    }

    public static List<Violation> generateMockViolationsForIssue(String bugId, String... checknames) {
        List<Violation> violations = new ArrayList<Violation>(checknames.length);
        for ( String checkname : checknames )
            violations.add(mockViolation(bugId, checkname));
        return violations;
    }

    public static Map<String,FlagStatus> mockStreamStatus(String streamName, FlagStatus flagStatus) {
        Map<String,FlagStatus> map = new HashMap<String,FlagStatus>(0);
        map.put(streamName, flagStatus);
        return map;
    }
}
