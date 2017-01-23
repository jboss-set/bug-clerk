package org.jboss.jbossset.bugclerk.reports;

import java.util.Collection;

import org.jboss.jbossset.bugclerk.Candidate;

public interface ReportEngine<T> {

    T createReport(Collection<Candidate> violationByBugId);
}
