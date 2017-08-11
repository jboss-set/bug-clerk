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
import org.jboss.set.aphrodite.domain.Stream;
import org.jboss.set.aphrodite.issue.trackers.jira.JiraIssue;
import org.jboss.set.aphrodite.issue.trackers.jira.JiraIssueHomeImpl;

import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Marek Marusic <mmarusic@redhat.com> on 6/21/17.
 */
public class GitHelper {

    public static Stream getStreamForIssue(JiraIssue issue, AphroditeClient aphroditeClient) {
        String streamName = getStreamNameFromIssue(issue);
        List<Stream> streams = aphroditeClient.getAllStreams();
        return streams.stream().filter(stream -> stream.getName().contains(streamName))
                .findFirst().orElse(new Stream("none"));
    }

    private static String getStreamNameFromIssue(JiraIssue issue) {
        if (JiraIssueHomeImpl.isIssueJBEAP(issue)) {
            String targetReleaseVersion = extractTargetReleaseVersion(issue);
            return "jboss-eap-" + (targetReleaseVersion.endsWith(".z") ?
                    targetReleaseVersion : getMajorVersion(targetReleaseVersion) + ".z.");
        } else {
            return "wildfly";
        }
    }

    private static String extractTargetReleaseVersion(JiraIssue issue) {
        final String releaseSuffix = ".GA";
        String version = issue.getStreamStatus().size() > 0 ? issue.getStreamStatus().keySet().iterator().next() : "";
        version = version.replace(releaseSuffix, "");
        return version;
    }

    private static String getMajorVersion(String targetReleaseVersion) {
        return targetReleaseVersion.split("\\.")[0];
    }

    public static List<String> getIncorrectPRs(JiraIssue issue, Stream stream) {
        return issue.getPullRequests().stream().filter(pr -> !isPRCorrect(pr, stream))
                .map(URL::toString).collect(Collectors.toList());
    }

    private static boolean isPRCorrect(URL pullRequest, Stream stream) {
        final String pull = "/pull/";
        final String commit = "/commit/";
        String pullRequestStr = pullRequest.toString();

        if (!pullRequestStr.contains(pull) && !pullRequestStr.contains(commit))
            return false;

        if (stream.getName().equals("none"))
            return true;

        return stream.getAllComponents().stream().anyMatch(s -> {
            // remove ".git" from repo string
            String repoToStr = s.getRepositoryURL().toString().replace(".git", "");
            return pullRequestStr.contains(repoToStr);
        });
    }
}
