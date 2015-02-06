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

package org.jboss.jbossset.bugclerk.bugzilla;

import static org.jboss.jbossset.bugclerk.utils.CollectionUtils.bugSetToIdStringSet;
import static org.jboss.jbossset.bugclerk.utils.CollectionUtils.indexViolationByCheckname;
import static org.jboss.jbossset.bugclerk.utils.StringUtils.ITEM_ID_SEPARATOR;
import static org.jboss.jbossset.bugclerk.utils.StringUtils.formatCheckname;
import static org.jboss.jbossset.bugclerk.utils.StringUtils.twoEOLs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;

import org.jboss.jbossset.bugclerk.Violation;
import org.jboss.pull.shared.connectors.bugzilla.Comment;

public class ReportViolationToBzEngine {

    private final String header;
    private final String footer;
    private final BugzillaClient bugzillaClient;


    public ReportViolationToBzEngine(String header, String footer, BugzillaClient bugzillaClient) {
        this.header = header;
        this.footer = footer;
        this.bugzillaClient = bugzillaClient;
    }

    public boolean reportViolationToBZ(Map<Integer, List<Violation>> violationByBugId) {

        return reportViolationToBugTracker(violationByBugId,
                bugzillaClient.loadCommentForBug(bugSetToIdStringSet(violationByBugId
                        .keySet())));
    }

    boolean reportViolationToBugTracker(Map<Integer, List<Violation>> violationByBugId, Map<String, SortedSet<Comment>> commentsByBugId) {
        for (Entry<Integer, List<Violation>> bugViolation : violationByBugId.entrySet()) {
            List<Violation> newViolationToReport = filterViolationsAlreadyReported(bugViolation.getValue(),
                    commentsByBugId.get(bugViolation.getKey().toString()));
            if (!newViolationToReport.isEmpty())
                return bugzillaClient.addPrivateCommentTo(bugViolation.getKey(),
                        messageBody(newViolationToReport, new StringBuffer(header)).append(footer).toString());
        }
        return false; // no violation reported
    }

    private List<Violation> filterViolationsAlreadyReported(List<Violation> violations, SortedSet<Comment> comments) {
        List<Violation> violationsToReport = new ArrayList<>(violations.size());
        for (Entry<String, Violation> entry : indexViolationByCheckname(violations).entrySet()) {
            CommentPatternMatcher matcher = new CommentPatternMatcher(formatCheckname(entry.getKey()));
            if (!matcher.containsPattern(comments))
                violationsToReport.add(entry.getValue());
        }
        return violationsToReport;
    }

    private StringBuffer messageBody(List<Violation> violations, StringBuffer text) {
        if (violations == null || violations.isEmpty() || "".equals(text))
            throw new IllegalArgumentException("No violations or text empty");

        int violationId = 1;
        for (Violation violation : violations)
            text.append(violationId++).append(ITEM_ID_SEPARATOR).append(formatCheckname(violation.getCheckName())).append(" ")
                    .append(violation.getMessage()).append(twoEOLs());
        return text;
    }
}
