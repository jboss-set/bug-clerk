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

import org.jboss.jbossset.bugclerk.Candidate;
import org.jboss.jbossset.bugclerk.Violation;

public class StringReportEngine implements ReportEngine<String> {

    @Override
    public String createReport(Collection<Candidate> violationByBugId) {
        return violationByBugId.isEmpty() ? "" : buildReportAsString(violationByBugId);
    }

    private static String buildReportAsString(Collection<Candidate> values) {
        return values.stream().map(candidate -> format(candidate)).reduce((s1, s2) -> s1.append(s2))
                .get().toString();
    }

    private static StringBuffer reportViolations(List<Violation> violations) {
        if ( violations.isEmpty() )
            return new StringBuffer();
        else
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

    private static StringBuffer format(Candidate candidate) {
        StringBuffer report = new StringBuffer();
        if (! candidate.getViolations().isEmpty() ) {
            report.append("Issue: ").append(candidate.getBug().getURL() + " - " + candidate.getBug().getSummary().get() ).append(EOL)
                    .append("\t has the following violations (" + candidate.getViolations().size() + "):").append(EOL).append(EOL);
            return report.append(reportViolations(candidate.getViolations())).append(twoEOLs());
        }
        else
            return report;
    }

}
