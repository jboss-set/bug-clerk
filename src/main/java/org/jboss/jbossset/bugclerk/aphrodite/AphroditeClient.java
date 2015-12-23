package org.jboss.jbossset.bugclerk.aphrodite;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jboss.jbossset.bugclerk.utils.URLUtils;
import org.jboss.set.aphrodite.Aphrodite;
import org.jboss.set.aphrodite.config.AphroditeConfig;
import org.jboss.set.aphrodite.config.IssueTrackerConfig;
import org.jboss.set.aphrodite.config.TrackerType;
import org.jboss.set.aphrodite.domain.Comment;
import org.jboss.set.aphrodite.domain.Issue;
import org.jboss.set.aphrodite.spi.AphroditeException;
import org.jboss.set.aphrodite.spi.NotFoundException;

public final class AphroditeClient {

    private final Aphrodite aphrodite;

    public AphroditeClient(AphroditeParameters parameters) {
        try {
            aphrodite = Aphrodite.instance(buildAphroditeConfig(parameters.getTrackerUrl(), parameters.getUsername(),
                    parameters.getPassword()));
        } catch (AphroditeException e) {
            throw new IllegalStateException(e);
        }
    }

    private static AphroditeConfig buildAphroditeConfig(String trackerUrl, String username, String password) {
        List<IssueTrackerConfig> issueTrackerConfigs = new ArrayList<>();
        issueTrackerConfigs.add(new IssueTrackerConfig(trackerUrl, username, password, TrackerType.BUGZILLA, 1000));
        return AphroditeConfig.issueTrackersOnly(issueTrackerConfigs);
    }

    public List<Issue> retrievePayload(String filterUrl) {
        try {
            return aphrodite.searchIssuesByFilter(URLUtils.createURLFromString(filterUrl));
        } catch (NotFoundException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public void addComments(Map<Issue, Comment> comments) {
        // FIXME: add a bulk method in Aphrodite
        for (Entry<Issue, Comment> entry : comments.entrySet())
            aphrodite.postCommentOnIssue(entry.getKey(), entry.getValue());
    }
}
