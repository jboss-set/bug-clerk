package org.jboss.jbossset.bugclerk.reports;

import java.util.List;
import java.util.Map;

import org.jboss.jbossset.bugclerk.Violation;
import org.jboss.set.aphrodite.domain.Issue;

public interface ReportEngine<T> {

    T createReport(Map<Issue, List<Violation>> violationByBugId);
}
