package org.jboss.jbossset.bugclerk;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportEngine {

    private final String urlPrefix;

    public ReportEngine(String urlPrefix) {
        this.urlPrefix = urlPrefix;
    }

    public String createReport(Collection<Violation> violations) {
        String reportString = "";
        if ( ! violations.isEmpty() ) {
            Map<Integer,List<Violation>> violationByBugId = indexedViolationsByBugId(violations);
            StringBuffer report = new StringBuffer();
            for ( List<Violation> violationBug : violationByBugId.values() ) {
                report = format(violationBug, report);
            }
            reportString = report.toString();
        }
        return reportString;
    }

    private Map<Integer,List<Violation>> indexedViolationsByBugId(Collection<Violation> violations) {
        Map<Integer,List<Violation>> violationIndexedByBugId = new HashMap<Integer, List<Violation>>(violations.size());
        for ( Violation violation : violations ) {
            int id = violation.getBug().getId();
            if ( ! violationIndexedByBugId.containsKey(id)) {
                List<Violation> violationsForBug = new ArrayList<Violation>();
                violationsForBug.add(violation);
                violationIndexedByBugId.put(id, violationsForBug);
            } else
                violationIndexedByBugId.get(id).add(violation);
        }
        return violationIndexedByBugId;
    }

    private static final String EOL = "\n";
    private StringBuffer format(List<Violation> violations, StringBuffer report) {
        int bugId = violations.get(0).getBug().getId();
        report.append("BZ").append(bugId).append(" - ").append(this.urlPrefix + bugId).append(EOL)
              .append("\t has the following violations (" + violations.size() + "):").append(EOL)
              .append(EOL);
        int violationId = 1;
        for ( Violation violation : violations ) {
            report.append(violationId++).append(") ").append(violation.getMessage()).append(EOL);
        }
        return report.append(EOL).append(EOL);
    }

}
