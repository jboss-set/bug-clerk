package org.jboss.jbossset.bugclerk.utils;

import static org.jboss.set.aphrodite.domain.IssueType.UPGRADE;

import java.net.URL;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.jboss.jbossset.bugclerk.aphrodite.AphroditeClient;
import org.jboss.set.aphrodite.domain.Comment;
import org.jboss.set.aphrodite.domain.FlagStatus;
import org.jboss.set.aphrodite.domain.Issue;
import org.jboss.set.aphrodite.domain.IssueStatus;
import org.jboss.set.aphrodite.domain.IssueType;
import org.jboss.set.aphrodite.domain.Release;
import org.jboss.set.aphrodite.domain.Stream;
import org.jboss.set.aphrodite.domain.StreamComponent;
import org.jboss.set.aphrodite.issue.trackers.jira.JiraChangelogGroup;
import org.jboss.set.aphrodite.issue.trackers.jira.JiraChangelogItem;
import org.jboss.set.aphrodite.issue.trackers.jira.JiraIssue;
/**
 * <p>Regroups a set of static method used by some checks.</p>
 *
 * @author Romain Pelisse - belaran@redhat.com
 *
 */
public final class RulesHelper {

    public static String PAYLOAD_TRACKER_PREFIX = "Payload Tracker";
    private final static String STATUS = "Status";
    private final static String RESOLVED = "Resolved";

    private RulesHelper(){}

    private static Optional<Issue> retrieveIssueIfNotFoundIn(URL block,Map<URL, Issue> payloadTrackerIndexedByURL, AphroditeClient aphrodite) {
        Optional<Issue> blockingIssue;
        Issue issue = payloadTrackerIndexedByURL.get(block);
        if ( issue == null && aphrodite != null ) // Aphrodite would 'null' only in unit test case
            blockingIssue = aphrodite.retrieveIssue(block);
        else
            blockingIssue = Optional.of(issue);
        return blockingIssue;
    }

    public static boolean isOneOfThoseIssueAPayload(List<URL> issues, Map<URL, Issue> payloadTrackerIndexedByURL, AphroditeClient aphrodite) {
        for ( URL block : issues ) {
            Optional<Issue> blockingIssue = retrieveIssueIfNotFoundIn(block, payloadTrackerIndexedByURL, aphrodite);
            if ( blockingIssue != null && blockingIssue.isPresent() && blockingIssue.get().getSummary().isPresent() && blockingIssue.get().getSummary().get().contains(PAYLOAD_TRACKER_PREFIX) )
                return true;
        }
        return false;
    }

    public static boolean isOneOfThoseIssueAComponentUpgrade(List<URL> issues, Map<URL, Issue> issuesIndexedByURL) {
        for ( URL block : issues ) {
            Issue issue = issuesIndexedByURL.get(block);
            if ( issue != null && issue.getType().equals(UPGRADE) )
                return true;
        }
        return false;
    }

    public static boolean doesAnIssueBelongToPayloadTracker(List<URL> issues, Map<URL, Issue> issuesIndexedByURL, Map<URL, Issue> payloadTrackerIndexedByURL) {
        for ( URL url : issues ) {
            Issue issue = issuesIndexedByURL.get(url);
            if ( issue != null && issue.getType().equals(IssueType.BUG) && payloadTrackerIndexedByURL.containsKey(url) )
                return true;
        }
        return false;
    }

    private static String extractVersionNumberPrefix(String versionName) {
        // Turns "7.0.0.CR1" or "7.0.z.GA" into "7.0"
        return versionName.substring(0, 3);
    }

    public static boolean releasesStreamMismatch(Issue issue) {
        List<Release> releases = issue.getReleases();
        Map<String, FlagStatus> streamStatus = issue.getStreamStatus();
        for ( Release release : releases ) {
            String versionPrefix = extractVersionNumberPrefix(release.getVersion().get());
            for ( String targetRelease : streamStatus.keySet() ) {
                String targetPrefix = extractVersionNumberPrefix(targetRelease);
                if ( ! targetPrefix.equals(versionPrefix) )
                    return true;
            }
        }
        return false;
    }

    public static boolean dependsOnContainsAtLeastOneJIRAIssue(Issue issue) {
        // WARNING: This method is designed to be used with BZ issue, thus having no JIRA issue
        //          load in memory. So instead of using the TrackerType, we look for 'jira' in
        //          url string. Bugclerk needs to be (greatly) improved to have a more appropriate
        //          behavior...
        List<URL> dependsOn = issue.getDependsOn();
        for ( URL url: dependsOn)
            if ( url.getHost().toLowerCase().contains(JIRA_TRACKER_HOSTNAME))
                return true;
        return false;
    }

    public static final String JIRA_TRACKER_HOSTNAME = "issues.jboss.org";

    public static boolean noUpstreamRequiredExplanation(List<Comment> comments) {
        for (Comment comment: comments ) {
            String commentBody = comment.getBody().toLowerCase();
            if ( commentBody.contains("no upstream") &&
                    commentBody.contains("required") )
                return true;
        }
        return false;
    }

    public static boolean doesSiblingsFixVersionsContainsParentsOne(Issue issue, Issue sibling) {
        for ( Release parentRelease : issue.getReleases() )
            if ( sibling.getReleases().contains(parentRelease) ) return true;
        return false;
    }

    public static boolean isPullRequestAgainstAppropriateBranch(JiraIssue issue, AphroditeClient aphrodite) {
        Release release = issue.getReleases().get(0);
        if ( release.getVersion().isPresent() ) {
            String branchName = release.getVersion().get();
            for (Stream stream : aphrodite.getAllStreams() )
                if ( stream.getName().contains(branchName))
                    if ( checkPullRequestsAgainstEachComponentCodebase(issue,stream.getAllComponents(), aphrodite ) )
                        return true;
        }
        return false;
    }

    public static boolean isInResolvedState(JiraIssue issue) {
        return issue.getStatus().equals(IssueStatus.MODIFIED);
    }

    public static boolean isChangedAfterResolved(List<JiraChangelogGroup> changelog) {
        Date resolvedDate = getLastResolvedDate(changelog);
        return changelog.stream().anyMatch(group -> resolvedDate.before(group.getCreated())
                && isNotAllowedChange(group.getItems()));
    }

    public static Date getLastResolvedDate(List<JiraChangelogGroup> changelog) {
        JiraChangelogGroup lastResolved = changelog.stream()
                .filter(group -> containsStatusChangeToResolved(group.getItems()))
                .reduce((first, second) -> second).orElse(null);
        return (lastResolved != null) ? lastResolved.getCreated() : new Date();
    }

    private static boolean containsStatusChangeToResolved(List<JiraChangelogItem> items) {
        return items.stream().anyMatch(RulesHelper::isStatusChangeToResolved);
    }

    private static boolean isStatusChangeToResolved(JiraChangelogItem item) {
        return item.getField().equalsIgnoreCase(STATUS) && item.getToString().equalsIgnoreCase(RESOLVED);
    }

    private static boolean isNotAllowedChange(List<JiraChangelogItem> items) {
        return items.stream().anyMatch(item -> !item.getField().equalsIgnoreCase("Status"));
    }

    private static boolean checkPullRequestsAgainstEachComponentCodebase(JiraIssue issue, Collection<StreamComponent> streams, AphroditeClient aphrodite) {
        for ( StreamComponent component : streams )
            if ( doesPullRequestsFilledAgainstAppropriateCodebase(issue.getPullRequests(),component,aphrodite) )
                return true;
        return false;
    }

    private static boolean doesPullRequestsFilledAgainstAppropriateCodebase(List<URL> pullRequests, StreamComponent component, AphroditeClient aphrodite) {
        for ( URL url : pullRequests)
            if ( url.toString().startsWith(component.getRepositoryURL().toString()))
                if ( ! component.getCodebase().equals(aphrodite.getPullRequest(url.toString()).getCodebase()))
                    return true;
        return false;
    }
}
