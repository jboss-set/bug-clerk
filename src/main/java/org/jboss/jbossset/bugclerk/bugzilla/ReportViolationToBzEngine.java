package org.jboss.jbossset.bugclerk.bugzilla;

import static org.jboss.jbossset.bugclerk.utils.StringUtils.ITEM_ID_SEPARATOR;
import static org.jboss.jbossset.bugclerk.utils.StringUtils.twoEOLs;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jboss.jbossset.bugclerk.Violation;
public class ReportViolationToBzEngine {

    private final String header;
    private final String footer;

    public ReportViolationToBzEngine(String header, String footer) {
        this.header = header;
        this.footer = footer;
    }

    public void reportViolationToBZ(Map<Integer, List<Violation>> violationByBugId) {
        BugzillaClient bugzillaClient = new BugzillaClient();
        for (Entry<Integer, List<Violation>> bugViolation : violationByBugId.entrySet())
            bugzillaClient.addPrivateCommentTo(bugViolation.getKey(),
                    messageBody(bugViolation.getValue(), new StringBuffer(header)).append(footer).toString());
    }

    private StringBuffer messageBody(List<Violation> violations, StringBuffer text) {
        int violationId = 1;
        for (Violation violation : violations)
            text.append(violationId++).append(ITEM_ID_SEPARATOR).append(violation.getMessage()).append(twoEOLs());
        return text;
    }
}
