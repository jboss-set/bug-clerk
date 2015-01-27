package org.jboss.jbossset.bugclerk.bugzilla;

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

public class BugzillaClient {

    private final BZHelper bugzillaHelper;

    public BugzillaClient() {
        bugzillaHelper = createHelper();
    }

    private static final String CONFIGURATION_FILENAME = "processor-eap-6.properties";
    private static final Logger LOGGER = LoggingUtils.getLogger();

    private static BZHelper createHelper() {
        try {
            return new BZHelper(CONFIGURATION_FILENAME,CONFIGURATION_FILENAME);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public Bug loadBzFromUrl(URL url) {
        return logBugRetrieved((Bug)this.bugzillaHelper.findIssue(url));
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

    public  SortedSet<Comment> loadCommentForBug(Bug bug) {
        return logRetrievedComments(this.bugzillaHelper.loadCommentsFor(bug));
    }

    public Map<String, SortedSet<Comment>> loadCommentForBug(Collection<String> bugIds) {
        return this.bugzillaHelper.loadCommentsFor(bugIds);
    }

    public Map<String,Bug> loadBugsById(Set<String> bugIds) {
            return this.bugzillaHelper.loadIssues(bugIds);
    }
}