package org.jboss.jbossset.bugclerk.checks;

import java.util.function.Predicate;

import org.jboss.set.aphrodite.issue.trackers.jira.JiraChangelogGroup;
import org.jboss.set.aphrodite.issue.trackers.jira.JiraChangelogItem;

public class PredicateLastDateOfStatusChangeTo implements Predicate<JiraChangelogGroup> {

    private static final String STATUS = "Status";

    final String status;

    public PredicateLastDateOfStatusChangeTo(String status) {
        this.status = status;
    }

    @Override
    public boolean test(JiraChangelogGroup changelog) {
        return changelog.getItems().stream().anyMatch(new Predicate<JiraChangelogItem>() {
            @Override
            public boolean test(JiraChangelogItem t) {
                return PredicateLastDateOfStatusChangeTo.isStatusChangeToStatus(t, status);
            }
        });
    }

    private static boolean isStatusChangeToStatus(JiraChangelogItem item, String status) {
        return item.getField().equalsIgnoreCase(STATUS) && item.getToString().equalsIgnoreCase(status);
    }
}
