package org.jboss.jbossset.bugclerk.utils;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jboss.jbossset.bugclerk.aphrodite.AphroditeClient;
import org.jboss.set.aphrodite.domain.PullRequest;

public class PullRequestUtils {
    private PullRequestUtils() {
    }

    public static Collection<PullRequest> fetchPullRequests(final AphroditeClient client, List<URL> urls) {
        final List<PullRequest> pullRequests = new ArrayList<>();
        for (URL url : urls) {
            pullRequests.add(client.getPullRequest(url.toString()));
        }
        return pullRequests;
    }
}
