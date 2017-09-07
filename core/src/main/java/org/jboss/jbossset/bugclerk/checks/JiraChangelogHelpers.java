package org.jboss.jbossset.bugclerk.checks;

import java.util.Date;
import java.util.List;
import java.util.function.Predicate;

import org.jboss.set.aphrodite.issue.trackers.jira.JiraChangelogGroup;
import org.jboss.set.aphrodite.issue.trackers.jira.JiraChangelogItem;
import org.jboss.set.aphrodite.issue.trackers.jira.JiraIssue;

public final class JiraChangelogHelpers {

    private static final String SPRINT_FIELD_NAME = "Sprint";

    private JiraChangelogHelpers() {}

    public static JiraChangelogGroup getLastFixVersionChangeDuringSprint(JiraIssue issue) {
        List<JiraChangelogGroup> changelog = issue.getChangelog();
        return changelog.stream().filter(group -> getLastSprint(changelog, issue.getSprintRelease()).before(group.getCreated()))
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
        return item.getField().equalsIgnoreCase(SPRINT_FIELD_NAME) && item.getToString().equalsIgnoreCase(sprintRelease);
    }

    public static boolean isChangedAfterSetToStatus(List<JiraChangelogGroup> changelog, String status) {
        return isChangedAfterDate(changelog, getLastSetToStatusDate(changelog, status), new CheckIfAnythingButStatusIsChanged());
    }

    public static boolean isStatusChangedAfterSetToStatus(List<JiraChangelogGroup> changelog, String status) {
        return isChangedAfterDate(changelog, getLastSetToStatusDate(changelog, status), new CheckIfStatusChanged() );
    }

    private static boolean isChangedAfterDate(List<JiraChangelogGroup> changelog, Date date, Predicate<List<JiraChangelogItem>> changelogFilteringPolicy) {
        return changelog.stream().anyMatch(group -> date.before(group.getCreated())
                && changelogFilteringPolicy.test(group.getItems()));
    }

    public static Date getLastSetToStatusDate(List<JiraChangelogGroup> changelog, String status) {
        return getDateUsingPredicate(changelog,new PredicateLastDateOfStatusChangeTo(status));
    }

    private static Date getDateUsingPredicate(List<JiraChangelogGroup> changelog, Predicate<JiraChangelogGroup> p ) {
        JiraChangelogGroup lastResolved = changelog.stream().filter(p)
                .reduce((first, second) -> second).orElse(null);
        return (lastResolved != null) ? lastResolved.getCreated() : new Date();
    }
}
