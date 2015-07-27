package org.jboss.jbossset.bugclerk.reports;

import java.util.List;
import java.util.Map;

import org.jboss.jbossset.bugclerk.Violation;

public interface ReportEngine<T> {

    T createReport(Map<Integer, List<Violation>> violationByBugId);
}
