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

import org.jboss.set.aphrodite.domain.Issue;
import org.jboss.set.aphrodite.domain.spi.IssueHome;
import org.jboss.set.aphrodite.issue.trackers.jira.JiraIssue;
import org.jboss.set.aphrodite.issue.trackers.jira.JiraIssueHomeImpl;
import org.jboss.set.aphrodite.issue.trackers.jira.JiraLabel;
import org.jboss.set.aphrodite.simplecontainer.SimpleContainer;

import javax.naming.NameNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Created by Marek Marusic <mmarusic@redhat.com> on 6/12/17.
 */
public final class LabelsHelper {
    public static final String JBEAPProject = "JBoss Enterprise Application Platform";
    public static final String DOWNSTREAM_DEP = "downstream_dependency";

    public static boolean isIssueJBEAP(JiraIssue issue) {
        return JiraIssueHomeImpl.isIssueJBEAP(issue);
    }

    public static List<Issue> getIssuesMissingDownstreamLabel(JiraIssue issue) {
        SimpleContainer container = (SimpleContainer) SimpleContainer.instance();
        IssueHome issueHome = new JiraIssueHomeImpl();
        container.register(IssueHome.class.getSimpleName(), issueHome);

        Stream<Issue> upstreamReferences = getUpstreamReferences(issue);

        List<Issue> issuesWithoutLabel = new ArrayList<>();
        upstreamReferences.filter(i -> isMissingDownstreamLabel((JiraIssue) i)).forEach(issuesWithoutLabel::add);

        return issuesWithoutLabel;
    }

    private static Stream<Issue> getUpstreamReferences(JiraIssue issue) {
        Stream<Issue> upstreamReferences = null;
        try {
            upstreamReferences = issue.getUpstreamReferences();
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return (upstreamReferences != null) ? upstreamReferences : new ArrayList<Issue>().stream();
    }

    private static boolean isMissingDownstreamLabel(JiraIssue issue) {
        JiraLabel downstreamDepLabel = new JiraLabel(DOWNSTREAM_DEP);
        return issue.getLabels().stream().noneMatch(downstreamDepLabel::equals);
    }

    public static String getLinksOfIssues(List<Issue> issues) {
        StringBuilder links = new StringBuilder();

        issues.forEach(i -> links.append(i.getURL()).append(" "));
        return links.toString();
    }
}
