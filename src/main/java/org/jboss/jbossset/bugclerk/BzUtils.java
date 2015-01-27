package org.jboss.jbossset.bugclerk;

import java.net.URL;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.jbossset.bugclerk.utils.LoggingUtils;
import org.jboss.pull.shared.connectors.bugzilla.BZHelper;
import org.jboss.pull.shared.connectors.bugzilla.Bug;
import org.jboss.pull.shared.connectors.bugzilla.Comment;
import org.jboss.pull.shared.connectors.common.Flag;

public final class BzUtils {

    private BzUtils() {
    }

    private static final String CONFIGURATION_FILENAME = "processor-eap-6.properties";
    private static final Logger LOGGER = LoggingUtils.getLogger();

    private static BZHelper createHelper() throws Exception {
        return new BZHelper(CONFIGURATION_FILENAME,CONFIGURATION_FILENAME);
    }

    public static Bug loadBzFromUrl(URL url) {
        try {
            return logBugRetrieved((Bug) createHelper().findIssue(url));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    private static Bug logBugRetrieved(Bug issue) {
        if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.fine("BZ" + issue.getNumber());
            LOGGER.fine("Status:" + issue.getStatus());
            LOGGER.fine("Summary:" + issue.getSummary());
            for (Flag flag : issue.getFlags())
                LOGGER.fine("Flag:" + flag.getName());
        }
        return issue;
    }

    private static SortedSet<Comment> logRetrievedComments(SortedSet<Comment> comments) {
        if (LOGGER.isLoggable(Level.FINE)) {
            for (Comment comment : comments) {
                LOGGER.fine("Comment ID:" + comment.getId());
                LOGGER.fine("Comment Text:" + comment.getText());
            }
        }
        return comments;
    }

    public static SortedSet<Comment> loadCommentForBug(Bug bug) {
        try {
            return logRetrievedComments(createHelper().loadCommentsFor(bug));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Map<String, SortedSet<Comment>> loadCommentForBug(Collection<String> bugIds) {
        try {
            return createHelper().loadCommentsFor(bugIds);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Map<String,Bug> loadBugsById(Set<String> bugIds) {
        try {
            return createHelper().loadIssues(bugIds);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
