package org.jboss.jbossset.bugclerk.checks;

import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

import org.jboss.jbossset.bugclerk.aphrodite.AphroditeClient;
import org.jboss.jbossset.bugclerk.utils.LoggingUtils;
import org.jboss.set.aphrodite.domain.Issue;
import org.jboss.set.aphrodite.domain.IssueType;
import org.jboss.set.aphrodite.domain.Release;

import static org.jboss.set.aphrodite.config.TrackerType.BUGZILLA;
import static org.jboss.set.aphrodite.config.TrackerType.JIRA;

@Deprecated // Those methods were designed to interract with BZ based payload tracker...
public final class PayloadHelpers {

    public static String PAYLOAD_TRACKER_PREFIX = "Payload Tracker";
    public static Pattern JIRA_PAYLOAD_VERSION_NAME = Pattern.compile("\\d\\.\\d\\.\\d+\\.GA");

    private PayloadHelpers() {}

    public static boolean isOneOfThoseIssueAPayload(List<URL> issues, Map<URL, Issue> payloadTrackerIndexedByURL, AphroditeClient aphrodite) {
        for ( URL block : issues ) {
            Optional<Issue> blockingIssue = RulesHelper.retrieveIssueIfNotFoundIn(block, payloadTrackerIndexedByURL, aphrodite);
            if ( blockingIssue != null && blockingIssue.isPresent() && blockingIssue.get().getSummary().isPresent() && blockingIssue.get().getSummary().get().contains(PAYLOAD_TRACKER_PREFIX) )
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

    private static boolean hasJiraPayloadVersion(Collection<Release> releases) {
        for (Release release : releases) {
            // simple check - it's a payload if the version uses only numbers and ends with .GA
            if (JIRA_PAYLOAD_VERSION_NAME.matcher(release.getVersion().get()).matches()) {
                return true;
            }
        }
        return false;
    }

    public static boolean isInPayload(Issue issue, Map<URL, Issue> issuesIndexedByURL, AphroditeClient aphrodite) {
        if (issue.getTrackerType() == JIRA) {
            return hasJiraPayloadVersion(issue.getReleases());
        } else if (issue.getTrackerType() == BUGZILLA) {
            return isOneOfThoseIssueAPayload(issue.getBlocks(), issuesIndexedByURL, aphrodite);
        } else {
            LoggingUtils.getLogger().warning("Unknown tracker type: " + issue.getTrackerType());
            return false;
        }
    }

}
