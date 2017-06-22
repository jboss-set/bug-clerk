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

import org.jboss.set.aphrodite.issue.trackers.jira.JiraIssue;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marek Marusic <mmarusic@redhat.com> on 6/21/17.
 */
public class GitHelper {

    public static List<String> getIncorrectPRs(JiraIssue issue) {
        List<String> incorrectPRs = new ArrayList<>();

        issue.getPullRequests().stream().filter(pr -> ! isPRCorrect(pr, getGitOrganization(issue.getURL())))
                .forEach(pr-> incorrectPRs.add(pr.toString()));

        return incorrectPRs;
    }

    private static boolean isPRCorrect(URL pullRequest, String githubOrganization) {
        final String github = "https://github.com";
        final String pull = "/pull/";
        final String commit = "/commit/";

        return pullRequest.toString().startsWith(github) && pullRequest.toString().contains(githubOrganization)
                && (pullRequest.toString().contains(pull) || pullRequest.toString().contains(commit));
    }

    private static String getGitOrganization(URL url) {
        final String JBEAP = "JBEAP";
        final String WFCORE = "WFCORE";
        final String WFLY = "WFLY";
        final String jbossOrganization = "jbossas";
        final String wildflyOrganization = "wildfly";

        String ghOrganization = "";
        if (url.toString().contains(JBEAP) )
            ghOrganization = jbossOrganization;
        if (url.toString().contains(WFCORE) || url.toString().contains(WFLY))
            ghOrganization = wildflyOrganization;
        return ghOrganization;
    }
}
