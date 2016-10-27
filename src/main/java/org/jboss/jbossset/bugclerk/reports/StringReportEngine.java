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

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.jboss.jbossset.bugclerk.Violation;
import org.jboss.set.aphrodite.domain.Issue;

public class StringReportEngine implements ReportEngine<String> {

    @Override
    public String createReport(Map<Issue, List<Violation>> violationByBugId) {
        return violationByBugId.isEmpty() ? "" : buildReportAsString(violationByBugId.values());
    }

    private static String buildReportAsString(Collection<List<Violation>> values) {
        return values.stream().map(violations -> format(violations)).reduce((s1, s2) -> s1.append(s2))
                .get().toString();
    }

    private static String getBugId(List<Violation> violations) {
        return violations.get(0).getCandidate().getBug().getURL().toString();
    }

    private static StringBuffer reportViolations(List<Violation> violations) {
        return violations
                .stream()
                .map(v -> formatViolation(v)).reduce((s1, s2) -> s1.append(s2)).get();
    }

    private static StringBuffer formatViolation(Violation v) {
        StringBuffer report = new StringBuffer();
        report.append(v.getCheckName()).append(ITEM_ID_SEPARATOR).append(" (" + v.getLevel() + ") ")
                .append(v.getMessage()).append(EOL);
        return report;
    }

    private static StringBuffer format(List<Violation> violations) {
        StringBuffer report = new StringBuffer();
        report.append("Issue: ").append(getBugId(violations)).append(EOL)
                .append("\t has the following violations (" + violations.size() + "):").append(EOL).append(EOL);
        return report.append(reportViolations(violations)).append(twoEOLs());
    }
}
