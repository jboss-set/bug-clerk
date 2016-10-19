package org.jboss.jbossset.bugclerk;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import org.jboss.jbossset.bugclerk.checks.utils.DateUtils;
import org.jboss.set.aphrodite.config.TrackerType;
import org.jboss.set.aphrodite.domain.*;
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
        return mockBug(buildURL(bugId), summary);
    }

    public static <T extends Issue> T mockBug(URL bugId, String summary, Class<T> clazz) {
        T mock = Mockito.mock(clazz);
        return (T) populateMock(bugId, summary, mock);
    }
    
    public static Issue mockBug(URL bugId, String summary) {
        Issue mock = Mockito.mock(Issue.class);
        return populateMock(bugId, summary, mock);
    }
    
    public static <T extends Issue> T populateMock(URL bugId, String summary, T mock) {
        final Optional<IssueEstimation> estimation = Optional.of(mockEstimation(8));
        List<Release> releases = mockReleases("6.4.0","");

        Mockito.when(mock.getURL()).thenReturn(bugId);
        Mockito.when(mock.getTrackerId()).thenReturn(Optional.of(bugId.getQuery().split("=")[1]));
        Mockito.when(mock.getTrackerType()).thenReturn(TrackerType.BUGZILLA);
        Mockito.when(mock.getSummary()).thenReturn(Optional.of(summary));
        Mockito.when(mock.getType()).thenReturn(IssueType.BUG);
        Mockito.when(mock.getEstimation()).thenReturn(estimation);
        Mockito.when(mock.getStatus()).thenReturn(IssueStatus.NEW);
        Mockito.when(mock.getStage()).thenReturn(mockStage());
        Mockito.when(mock.getLastUpdated()).thenReturn(Optional.of(DateUtils.threeWeeksAgo()));
        Mockito.when(mock.getCreationTime()).thenReturn(Optional.of(DateUtils.threeMonthAgo()));
        Mockito.when(mock.getAssignee()).thenReturn(Optional.of(User.createWithEmail("jboss-set@redhat.com")));
        Mockito.when(mock.getReporter()).thenReturn(Optional.of(User.createWithEmail("Romain Pelisse <belaran@redhat.com>")));
        Mockito.when(mock.getReleases()).thenReturn(releases);
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
        Issue bug = MockUtils.mockBug(bugId, "summary");
        Violation mock = Mockito.mock(Violation.class);
        Mockito.when(mock.getCandidate()).thenReturn(Mockito.mock(Candidate.class));
        Mockito.when(mock.getCandidate().getBug()).thenReturn(bug);
        Mockito.when(mock.getCheckName()).thenReturn(checkname);
        Mockito.when(mock.getMessage()).thenReturn("Message for " + checkname + ".");
        Mockito.when(mock.getLevel()).thenReturn(Level.ERROR);
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
}
