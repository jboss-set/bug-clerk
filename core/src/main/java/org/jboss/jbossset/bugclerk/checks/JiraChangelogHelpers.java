package org.jboss.jbossset.bugclerk.checks;

import java.util.Date;
import java.util.List;

import org.jboss.set.aphrodite.issue.trackers.jira.JiraChangelogGroup;
import org.jboss.set.aphrodite.issue.trackers.jira.JiraChangelogItem;
import org.jboss.set.aphrodite.issue.trackers.jira.JiraIssue;

public final class JiraChangelogHelpers {

    private static final String STATUS = "Status";
    private static final String RESOLVED = "Resolved";

    private JiraChangelogHelpers() {}

    public static JiraChangelogGroup getLastFixVersionChangeDuringSprint(JiraIssue issue) {
        List<JiraChangelogGroup> changelog = issue.getChangelog();
        Date lastSprintDate = getLastSprint(changelog, issue.getSprintRelease());

        return changelog.stream().filter(group -> lastSprintDate.before(group.getCreated()))
                .reduce((first, second) -> second).orElse(null);
    }

    private static Date getLastSprint(List<JiraChangelogGroup> changelog, String sprintRelease) {
        JiraChangelogGroup lastSprint = changelog.stream().filter(group -> containsSprintChange(group.getItems(), sprintRelease))
                .reduce((first, second) -> second).orElse(null);
        return (lastSprint != null) ? lastSprint.getCreated() : new Date();
    }

    private static boolean containsSprintChange(List<JiraChangelogItem> items, String sprintRelease) {
        return items.stream().anyMatch(item -> isSprintChange(item, sprintRelease));
    }

    private static boolean isSprintChange(JiraChangelogItem item, String sprintRelease) {
        String toString = item.getToString();
        String field = item.getField();
        return field.equalsIgnoreCase("Sprint") && toString.equalsIgnoreCase(sprintRelease);
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
        return items.stream().anyMatch(JiraChangelogHelpers::isStatusChangeToResolved);
    }

    private static boolean isStatusChangeToResolved(JiraChangelogItem item) {
        return item.getField().equalsIgnoreCase(STATUS) && item.getToString().equalsIgnoreCase(RESOLVED);
    }


    private static boolean isNotAllowedChange(List<JiraChangelogItem> items) {
        return items.stream().anyMatch(item -> !item.getField().equalsIgnoreCase("Status"));
    }


}
