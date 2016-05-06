package org.jboss.jbossset.bugclerk.utils;

import java.net.URL;
import java.util.List;
import java.util.Map;

import org.jboss.set.aphrodite.domain.Issue;
import static org.jboss.set.aphrodite.domain.IssueType.UPGRADE;
/**
 * Regroups a set of static method used by some checks.</p>
 *
 * @author Romain Pelisse <belaran@redhat.com>
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
}
