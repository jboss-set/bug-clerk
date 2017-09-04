package org.jboss.jbossset.bugclerk.aphrodite;

import static org.jboss.jbossset.bugclerk.utils.ThreadUtil.execute;

import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.jboss.jbossset.bugclerk.aphrodite.callables.AddCommentTask;
import org.jboss.jbossset.bugclerk.aphrodite.callables.AllStreamsTask;
import org.jboss.jbossset.bugclerk.aphrodite.callables.GetPullRequest;
import org.jboss.jbossset.bugclerk.aphrodite.callables.LoadIssuesTask;
import org.jboss.jbossset.bugclerk.aphrodite.callables.RetrieveIssueTask;
import org.jboss.jbossset.bugclerk.aphrodite.callables.SearchIssueByFilterTask;
import org.jboss.jbossset.bugclerk.utils.URLUtils;
import org.jboss.set.aphrodite.Aphrodite;
import org.jboss.set.aphrodite.config.AphroditeConfig;
import org.jboss.set.aphrodite.config.IssueTrackerConfig;
import org.jboss.set.aphrodite.config.TrackerType;
import org.jboss.set.aphrodite.domain.Comment;
import org.jboss.set.aphrodite.domain.Issue;
import org.jboss.set.aphrodite.domain.PullRequest;
import org.jboss.set.aphrodite.domain.Stream;
import org.jboss.set.aphrodite.spi.AphroditeException;

public class AphroditeClient {

    private final Aphrodite aphrodite;

    private static final int DEFAULT_ISSUE_LIMIT = 400;

    private List<Stream> allStreams;

    public AphroditeClient() {
        try {
            aphrodite = Aphrodite.instance();
        } catch (Throwable e) {
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

    public static IssueTrackerConfig buildTrackerConfig(String trackerUrl, String username, String password, TrackerType type) {
        return new IssueTrackerConfig(URLUtils.getServerUrl(trackerUrl), username, password, type, DEFAULT_ISSUE_LIMIT);
    }

    public List<Issue> retrievePayload(String filterUrl) {
        return execute(new SearchIssueByFilterTask(aphrodite, filterUrl));
    }

    public void addComments(Map<Issue, Comment> comments) {
        if (!Boolean.valueOf(System.getProperty("bugclerk.comments.dryRun")))
            execute(new AddCommentTask(aphrodite, comments));
        else
            System.out.println("Updating comment is disable (dry run enabled), thus " + comments.size()
                    + " issues were NOT updated with Bugclerk messages.");
    }

    public List<Issue> loadIssues(List<String> ids) {
        return execute(new LoadIssuesTask(aphrodite, ids));
    }

    public List<Issue> loadIssuesFromUrls(List<URL> urls) {
        return aphrodite.getIssues(urls);
    }

    public Optional<Issue> retrieveIssue(URL url) {
        return ifNullReturnsEmpty(execute(new RetrieveIssueTask(aphrodite, url)));
    }

    private static <T> Optional<T> ifNullReturnsEmpty(T value) {
        return (value == null ? Optional.empty() : Optional.of(value));
    }

    public List<Stream> getAllStreams() {
        //Lazy loading of the streams
        if (allStreams == null) {
            allStreams = execute(new AllStreamsTask(aphrodite));
        }
        return allStreams;
    }

    public PullRequest getPullRequest(String pullRequestUrl) {
        return execute(new GetPullRequest(aphrodite, URLUtils.createURLFromString(pullRequestUrl)));
    }

    public void close() {
        try {
            aphrodite.close();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
