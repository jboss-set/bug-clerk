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
import org.jboss.set.aphrodite.domain.Issue;
import org.jboss.set.aphrodite.domain.IssueType;
import org.jboss.set.aphrodite.domain.Release;
import org.jboss.set.aphrodite.issue.trackers.jira.JiraIssue;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marek Marusic <mmarusic@redhat.com> on 7/11/17.
 *
 * Helper for the fix version checks.
 */
public class FixVersionHelper {
    public static final String GA = "GA";

    public static boolean isIssueUpgrade(JiraIssue issue) {
        return issue.getType() != null && issue.getType().equals(IssueType.UPGRADE);
    }

    public static List<Issue> getIncorporatedIssuesWithWrongFixVersionOrSprint(JiraIssue issue, AphroditeClient aphrodite) {
        List<Issue> incorporatedIssues = getIncorporatedIssues(issue, aphrodite);
        return filterIssuesWithDifferentFixVersionOrSprint(issue, incorporatedIssues);
    }

    private static List<Issue> getIncorporatedIssues(JiraIssue issue, AphroditeClient aphrodite) {
        return aphrodite.loadIssuesFromUrls(issue.getLinkedIncorporatesIssues());
    }

    static List<Issue> filterIssuesWithDifferentFixVersionOrSprint(JiraIssue issue, List<Issue> incorporatedIssues) {

        List<Issue> issuesWithWrongFixVersion = new ArrayList<>();
        incorporatedIssues.stream()
                .filter(i -> !haveIssuesEqualSpringAndFixVersion(issue, (JiraIssue) i))
                .forEach(issuesWithWrongFixVersion::add);

        return issuesWithWrongFixVersion;
    }

    private static boolean haveIssuesEqualSpringAndFixVersion(JiraIssue issue, JiraIssue i) {
        Release upgradeIssueGARelease = findFirstGARelease(issue.getReleases());

        return upgradeIssueGARelease.equals(findFirstGARelease(i.getReleases()))
                && issue.getSprintRelease().equals(i.getSprintRelease());
    }

    private static Release findFirstGARelease(List<Release> releases) {
        if (releases == null)
            return null;
        return releases.stream().filter(FixVersionHelper::isReleaseGA).findFirst().orElse(null);
    }

    private static boolean isReleaseGA(Release r) {
        return r.getVersion().isPresent() && r.getVersion().get().contains(GA);
    }
}
