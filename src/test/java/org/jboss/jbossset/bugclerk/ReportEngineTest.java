package org.jboss.jbossset.bugclerk;

import static org.junit.Assert.assertTrue;

import java.util.stream.Collectors;

import org.jboss.jbossset.bugclerk.reports.ReportEngine;
import org.jboss.jbossset.bugclerk.reports.StringReportEngine;
import org.junit.Test;

public class ReportEngineTest {

    @Test
    public void test() {
        final String dummyUrl = "https://bugzilla.redhat.com/show_bug.cgi?id=168875";
        final String bugId = "168875";
        final String checkname = "checkname";

        ReportEngine<String> engine = new StringReportEngine();
        String report = engine.createReport(MockUtils.mockViolationsListWithOneItem(bugId, checkname).parallelStream()
                .collect(Collectors.groupingBy(v -> v.getCandidate().getBug())));
        assertTrue(report.contains(dummyUrl));
        assertTrue(report.contains(checkname));
    }
}
