package org.jboss.jbossset.bugclerk.checks;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.jboss.jbossset.bugclerk.aphrodite.AphroditeClient;
import org.jboss.set.aphrodite.domain.Issue;
import org.jboss.set.aphrodite.domain.IssueType;

@Deprecated // Those methods were designed to interract with BZ based payload tracker...
public final class PayloadHelpers {

    public static String PAYLOAD_TRACKER_PREFIX = "Payload Tracker";

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

}
