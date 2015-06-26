package org.jboss.jbossset.bugclerk.github;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.jboss.jbossset.bugclerk.utils.URLUtils;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

public class GithubClient {

    final GitHub github;

    public GithubClient(String username, String password) {
        try {
            github = GitHub.connectUsingPassword(username, password);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    // FIXME: To improve with Closure
    public List<GHPullRequest> extractPullRequestsFromText(String text) {
        System.out.println("Invoked with text:" + text);
        List<GHPullRequest> pullRequests = new ArrayList<GHPullRequest>(0);
        for (String url : URLUtils.extractUrls(text)) {
            pullRequests.add(tranformIntoPullRequestOrNull(url));
        }
        return pullRequests;
    }

    public GHPullRequest tranformIntoPullRequestOrNull(String url) {
        if (url.contains("github.com") && url.contains("pull") && !url.endsWith("commits") && !url.endsWith("files")
                && !url.contains("#")) {
            transformURLintoPR(url);
        }
        return null;
    }

    private GHPullRequest transformURLintoPR(String url) {
        try {
            return transformURLintoPR(new URL(url));
        } catch (MalformedURLException e) {
            System.out.println("Malformed Pull Request URL:" + url);
        }
        return null;
    }

    private GHPullRequest transformURLintoPR(URL pullRequestURL) {
        final String path = pullRequestURL.getPath();
        if (!"".equals(path)) {
            return getPRfromReponameAndId(path.substring(1, path.indexOf("pull"))
                    , Integer.valueOf(path.substring(path.lastIndexOf('/') + 1)));
        }
        return null;
    }

    private GHPullRequest getPRfromReponameAndId(String repoName, int prId) {
        try {
            GHRepository repo = github.getRepository(repoName);
            return repo.getPullRequest(prId);
        } catch (IOException e) {
            System.out.println("Could not transform PR [" + prId + " from repo:" + repoName + ". Error:" + e.getMessage());
        }
        return null;
    }
}
