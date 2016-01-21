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

package org.jboss.jbossset.bugclerk.comments;

import static org.jboss.jbossset.bugclerk.utils.StringUtils.ITEM_ID_SEPARATOR;
import static org.jboss.jbossset.bugclerk.utils.StringUtils.formatCheckname;
import static org.jboss.jbossset.bugclerk.utils.StringUtils.twoEOLs;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.jboss.jbossset.bugclerk.BugClerk;
import org.jboss.jbossset.bugclerk.Level;
import org.jboss.jbossset.bugclerk.Violation;
import org.jboss.jbossset.bugclerk.utils.StringUtils;
import org.jboss.set.aphrodite.domain.Comment;
import org.jboss.set.aphrodite.domain.Issue;

public class ViolationsReportAsCommentBuilder {

    private static final String NOW = new SimpleDateFormat("yyyy/MM/dd - HH:mm").format(Calendar.getInstance().getTime());

    private static final String BUGCLERK_ISSUES_TRACKER = "https://github.com/jboss-set/bug-clerk/issues";

    private static final String COMMENT_MESSSAGE_HEADER = BugClerk.class.getSimpleName() + " (automated tool) noticed on "
            + NOW + " the following" + " discrepencies in this entry:" + StringUtils.twoEOLs();

    private static final String COMMENT_MESSAGE_FOOTER = "If the issues reported are erronous "
            + "or if you wish to ask for enhancement or new checks for " + BugClerk.class.getSimpleName()
            + " please, fill an issue on BugClerk issue tracker: " + BUGCLERK_ISSUES_TRACKER;

    public Map<Issue, Comment> reportViolationToBugTracker(Map<Issue, List<Violation>> violationByBugId) {
        Map<Issue, Comment> commentsToAddToIssues = new HashMap<Issue, Comment>();
        violationByBugId.forEach((k, violations) -> {
            Comment comment = buildCommentReportIfNotAlreadyReported(violations);
            if (comment != null)
                commentsToAddToIssues.put(k, comment);
        });
        return commentsToAddToIssues;
    }

    private Comment buildCommentReportIfNotAlreadyReported(List<Violation> violations) {
        List<Violation> newViolationToReport = filterViolationsAlreadyReported(violations);
        if (!newViolationToReport.isEmpty()) {
            return buildReportComment(newViolationToReport.stream().filter(v -> v.getLevel() == Level.ERROR)
                    .collect(Collectors.toList()));
        }
        return null;
    }

    private Comment buildReportComment(List<Violation> newViolationToReport) {
        if (!newViolationToReport.isEmpty())
            return new Comment(messageBody(newViolationToReport, new StringBuffer(COMMENT_MESSSAGE_HEADER)).append(
                    COMMENT_MESSAGE_FOOTER).toString(), true);
        return null;
    }

    private List<Violation> filterViolationsAlreadyReported(List<Violation> violations) {
        List<Violation> violationsToReport = new ArrayList<>(violations.size());
        Map<String, Violation> indexedByCheckname = violations.stream().collect(
                Collectors.toMap(k -> ((Violation) k).getCheckName(), v -> v));
        indexedByCheckname.forEach((k, v) -> {
            addViolationToReportIfNotAlreadyReported(formatCheckname(k), v.getCandidate().getBug().getComments(),
                    violationsToReport, v);
        });
        return violationsToReport;
    }

    private void addViolationToReportIfNotAlreadyReported(String checkname, List<Comment> comments,
            List<Violation> violationsToReport, Violation v) {
        CommentPatternMatcher matcher = new CommentPatternMatcher(checkname);
        if (!matcher.containsPattern(comments))
            violationsToReport.add(v);
    }

    private StringBuffer messageBody(List<Violation> violations, StringBuffer text) {
        if (violations == null || violations.isEmpty() || "".equals(text))
            throw new IllegalArgumentException("No violations or text empty");
        int violationId = 1;
        for (Violation violation : violations)
            addViolationToCommentReport(text, violationId++, violation);
        return text;
    }

    private void addViolationToCommentReport(StringBuffer text, int violationId, Violation violation) {
        text.append(violationId).append(ITEM_ID_SEPARATOR).append(formatCheckname(violation.getCheckName())).append(" ")
                .append(violation.getMessage()).append(twoEOLs());
    }
}
