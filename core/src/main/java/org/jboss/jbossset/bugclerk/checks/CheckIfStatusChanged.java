package org.jboss.jbossset.bugclerk.checks;

import java.util.List;
import java.util.function.Predicate;

import org.jboss.set.aphrodite.issue.trackers.jira.JiraChangelogItem;

public class CheckIfStatusChanged implements Predicate<List<JiraChangelogItem>> {

    protected static final String STATUS_FIELD_NAME = "Status";

    @Override
    public boolean test(List<JiraChangelogItem> items) {
        return items.stream().anyMatch(getFilterStrategy());
    }

    protected Predicate<JiraChangelogItem> getFilterStrategy() {
        return new FilterStatusItem();
    }

    private class FilterStatusItem implements Predicate<JiraChangelogItem> {

        @Override
        public boolean test(JiraChangelogItem item) {
            return item.getField().equalsIgnoreCase(STATUS_FIELD_NAME);
        }
    }
}
