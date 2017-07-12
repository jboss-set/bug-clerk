package org.jboss.jbossset.bugclerk.aphrodite.callables;

import java.net.URL;

import org.jboss.set.aphrodite.Aphrodite;
import org.jboss.set.aphrodite.domain.Issue;
import org.jboss.set.aphrodite.spi.NotFoundException;

public class RetrieveIssueTask extends AphroditeCallable<Issue> {

    private final URL url;

    public RetrieveIssueTask(Aphrodite aphrodite, URL url) {
        super(aphrodite);
        this.url = url;
    }

    @Override
    public Issue call() throws Exception, NotFoundException {
        try {
            return aphrodite.getIssue(url);
        } catch (NotFoundException e) {
            return null;
        }
    }

}
