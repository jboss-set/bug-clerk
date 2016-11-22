package org.jboss.jbossset.bugclerk.utils;

import static org.jboss.set.aphrodite.domain.IssueType.UPGRADE;

import java.net.URL;
import java.util.List;
import java.util.Map;

import org.jboss.set.aphrodite.domain.Comment;
import org.jboss.set.aphrodite.domain.FlagStatus;
import org.jboss.set.aphrodite.domain.Issue;
import org.jboss.set.aphrodite.domain.IssueType;
import org.jboss.set.aphrodite.domain.Release;
/**
 * <p>Regroups a set of static method used by some checks.</p>
 *
 * @author Romain Pelisse - belaran@redhat.com
 *
 */
public final class RulesHelper {

    public static String PAYLOAD_TRACKER_PREFIX = "Payload Tracker";

    private RulesHelper(){}

    public static boolean isOneOfThoseIssueAPayload(List<URL> issues, Map<URL, Issue> payloadTrackerIndexedByURL) {
        for ( URL block : issues ) {
            Issue issue = payloadTrackerIndexedByURL.get(block);
            if ( issue != null && issue.getSummary().isPresent() && issue.getSummary().get().contains(PAYLOAD_TRACKER_PREFIX) )
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
            if ( url.getHost().toLowerCase().contains("jira"))
                return true;
        return false;
    }

    public static boolean noUpstreamRequiredExplanation(List<Comment> comments) {
        for (Comment comment: comments ) {
            String commentBody = comment.getBody().toLowerCase();
            if ( commentBody.contains("no upstream") &&
                    commentBody.contains("required") )
                return true;
        }
        return false;
    }
}
