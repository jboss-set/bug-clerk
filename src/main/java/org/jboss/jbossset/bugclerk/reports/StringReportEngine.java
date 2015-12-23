/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2015, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.jbossset.bugclerk.reports;

import static org.jboss.jbossset.bugclerk.utils.StringUtils.EOL;
import static org.jboss.jbossset.bugclerk.utils.StringUtils.ITEM_ID_SEPARATOR;
import static org.jboss.jbossset.bugclerk.utils.StringUtils.twoEOLs;

import java.util.List;
import java.util.Map;

import org.jboss.jbossset.bugclerk.Violation;
import org.jboss.set.aphrodite.domain.Issue;

public class StringReportEngine implements ReportEngine<String> {

    private final String urlPrefix;

    public StringReportEngine(String urlPrefix) {
        this.urlPrefix = urlPrefix;
    }

    @Override
    public String createReport(Map<Issue, List<Violation>> violationByBugId) {
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
        String bugId = violations.get(0).getCandidate().getBug().getTrackerId().get();
        report.append("BZ").append(bugId).append(" - ").append(this.urlPrefix + bugId).append(EOL)
                .append("\t has the following violations (" + violations.size() + "):").append(EOL).append(EOL);
        int violationId = 1;
        for (Violation violation : violations)
            report.append(violationId++).append(ITEM_ID_SEPARATOR).append(" (" + violation.getLevel() + ") ")
                    .append(violation.getMessage()).append(EOL);
        return report.append(twoEOLs());
    }
}
