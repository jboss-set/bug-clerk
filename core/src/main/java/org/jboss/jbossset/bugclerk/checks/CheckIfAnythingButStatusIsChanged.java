package org.jboss.jbossset.bugclerk.checks;

import java.util.function.Predicate;

import org.jboss.set.aphrodite.issue.trackers.jira.JiraChangelogItem;

public class CheckIfAnythingButStatusIsChanged extends CheckIfStatusChanged {

    @Override
    protected Predicate<JiraChangelogItem> getFilterStrategy() {
        return new NonStatusItemFilter();
    }

    class NonStatusItemFilter implements Predicate<JiraChangelogItem> {

        @Override
        public boolean test(JiraChangelogItem item) {
            boolean status = ! item.getField().equalsIgnoreCase(STATUS_FIELD_NAME);
            return status;
        }
    }
}

