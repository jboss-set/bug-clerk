package org.jboss.jbossset.bugclerk.aphrodite;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.jboss.jbossset.bugclerk.utils.URLUtils;
import org.jboss.set.aphrodite.Aphrodite;
import org.jboss.set.aphrodite.config.AphroditeConfig;
import org.jboss.set.aphrodite.config.IssueTrackerConfig;
import org.jboss.set.aphrodite.config.TrackerType;
import org.jboss.set.aphrodite.domain.Comment;
import org.jboss.set.aphrodite.domain.Issue;
import org.jboss.set.aphrodite.domain.Patch;
import org.jboss.set.aphrodite.domain.Stream;
import org.jboss.set.aphrodite.spi.AphroditeException;
import org.jboss.set.aphrodite.spi.NotFoundException;

public class AphroditeClient {

    private final Aphrodite aphrodite;

    private static final int DEFAULT_ISSUE_LIMIT = 400;

    public AphroditeClient() {
        try {
            aphrodite = Aphrodite.instance();
        } catch (AphroditeException e) {
            throw new IllegalStateException(e);
        }
    }

    public AphroditeClient(IssueTrackerConfig issueTrackerConfig) {
        try {
            aphrodite = Aphrodite.instance(AphroditeConfig.issueTrackersOnly(Collections.singletonList(issueTrackerConfig)));
        } catch (AphroditeException e) {
            throw new IllegalStateException(e);
        }
    }

    public List<Issue> retrievePayload(String filterUrl) {
        try {
            return aphrodite.searchIssuesByFilter(URLUtils.createURLFromString(filterUrl));
        } catch (NotFoundException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public void addComments(Map<Issue, Comment> comments) {
        if ( ! Boolean.valueOf(System.getProperty("bugclerk.comments.dryRun")) )
            aphrodite.addCommentToIssue(comments);
        else
            System.out.println("Updating comment is disable (dry run enabled), thus " + comments.size() + " issues were NOT updated with Bugclerk messages.");
    }

    public List<Issue> loadIssues(List<String> ids) {
        return aphrodite.getIssues(ids.parallelStream().map(id -> URLUtils.createURLFromString(id))
                .collect(Collectors.toList()));
    }

    public List<Stream> getAllStreams() {
        return aphrodite.getAllStreams();
    }

    public static IssueTrackerConfig buildTrackerConfig(String trackerUrl, String username, String password, TrackerType type) {
        return new IssueTrackerConfig(URLUtils.getServerUrl(trackerUrl), username, password, type, DEFAULT_ISSUE_LIMIT);
    }

    public Patch getPullRequest(String pullRequestUrl) {
        try {
            return aphrodite.getPatch(URLUtils.createURLFromString(pullRequestUrl));
        } catch (NotFoundException e) {
            throw new IllegalArgumentException("No such Pull Requests:" + pullRequestUrl, e);
        }
    }

    public void close()  {
        try {
            aphrodite.close();
        } catch ( Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
