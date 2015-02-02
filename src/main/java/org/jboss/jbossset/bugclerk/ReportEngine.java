package org.jboss.jbossset.bugclerk;

import static org.jboss.jbossset.bugclerk.utils.StringUtils.EOL;
import static org.jboss.jbossset.bugclerk.utils.StringUtils.ITEM_ID_SEPARATOR;
import static org.jboss.jbossset.bugclerk.utils.StringUtils.twoEOLs;

import java.util.List;
import java.util.Map;

public class ReportEngine {

    private final String urlPrefix;

    public ReportEngine(String urlPrefix) {
        this.urlPrefix = urlPrefix;
    }

    public String createReport(Map<Integer, List<Violation>> violationByBugId) {
        String reportString = "";
        if (!violationByBugId.isEmpty()) {
            StringBuffer report = new StringBuffer();
            for (List<Violation> violationBug : violationByBugId.values()) {
                report = format(violationBug, report);
            }
            reportString = report.toString();
        }
        return reportString;
    }

    private StringBuffer format(List<Violation> violations, StringBuffer report) {
        int bugId = violations.get(0).getBug().getId();
        report.append("BZ").append(bugId).append(" - ").append(this.urlPrefix + bugId).append(EOL)
                .append("\t has the following violations (" + violations.size() + "):").append(EOL).append(EOL);
        int violationId = 1;
        for (Violation violation : violations)
            report.append(violationId++).append(ITEM_ID_SEPARATOR).append(violation.getMessage()).append(EOL);
        return report.append(twoEOLs());
    }
}
