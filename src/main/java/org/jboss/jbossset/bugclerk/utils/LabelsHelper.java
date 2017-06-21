/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2017, Red Hat, Inc., and individual contributors
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

package org.jboss.jbossset.bugclerk.utils;

import org.jboss.jbossset.bugclerk.aphrodite.AphroditeClient;
import org.jboss.set.aphrodite.domain.FlagStatus;
import org.jboss.set.aphrodite.domain.Issue;
import org.jboss.set.aphrodite.issue.trackers.jira.JiraIssue;
import org.jboss.set.aphrodite.issue.trackers.jira.JiraLabel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Marek Marusic <mmarusic@redhat.com> on 6/12/17.
 */
public final class LabelsHelper {
    public static final String JBEAPProject = "JBoss Enterprise Application Platform";
    public static final String DOWNSTREAM_DEP = "downstream_dependency";

    public static List<Issue> getIssuesMissingDownstreamLabel(JiraIssue issue, AphroditeClient aphrodite) {
        List<Issue> issues = aphrodite.loadIssues(findLinkedIssues(issue));
        List<Issue> issuesWithoutLabel = new ArrayList<>();
        issues.stream().filter(i -> isUpstreamIssue((JiraIssue) i, issue)
                && isMissingDownstreamLabel((JiraIssue) i)).forEach(issuesWithoutLabel::add);

        return issuesWithoutLabel;
    }

    public static List<String> findLinkedIssues(JiraIssue issue) {
        List<String> upstreamIssues = new ArrayList<>();
        if (issue.getDependsOn() != null)
            issue.getDependsOn().forEach(i -> upstreamIssues.add(i.toString()));
        if (issue.getBlocks() != null)
            issue.getBlocks().forEach(i -> upstreamIssues.add(i.toString()));

        return upstreamIssues;
    }

    private static boolean isUpstreamIssue(JiraIssue upstreamIssue, JiraIssue downstreamIssue) {
        if (upstreamIssue == null || downstreamIssue == null)
            return false;

        if (!isIssueJBEAP(upstreamIssue))
            return true;

        String upstreamRelease = extractTargetRelease(upstreamIssue.getStreamStatus());
        String downstreamRelease = extractTargetRelease(downstreamIssue.getStreamStatus());

        return VersionComparator.isFirstVersionHigher(upstreamRelease, downstreamRelease);
    }

    public static boolean isIssueJBEAP(JiraIssue issue) {
        return issue != null && issue.getProduct() != null && issue.getProduct().isPresent()
                && issue.getProduct().get().equals(JBEAPProject);
    }

    private static String extractTargetRelease(Map<String, FlagStatus> streamStatus) {
        // There should be max 1 key with value = FlagStatus.ACCEPTED or null in the stream status
        return (streamStatus != null && streamStatus.size() > 0) ? streamStatus.keySet().iterator().next() : "";
    }

    private static boolean isMissingDownstreamLabel(JiraIssue issue) {
        JiraLabel downstreamDepLabel = new JiraLabel(DOWNSTREAM_DEP);
        return issue.getLabels().stream().noneMatch(downstreamDepLabel::equals);
    }

    public static boolean areMissingDownstremDepLabel(List<Issue> upstreamIssuesMissingLabel) {
        return upstreamIssuesMissingLabel.size() != 0;
    }

    public static String getLinksOfIssues(List<Issue> issues) {
        StringBuilder links = new StringBuilder();

        issues.forEach(i -> links.append(i.getURL()).append(" "));
        return links.toString();
    }

}
