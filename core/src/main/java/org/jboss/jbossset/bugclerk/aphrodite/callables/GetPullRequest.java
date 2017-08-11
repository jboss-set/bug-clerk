package org.jboss.jbossset.bugclerk.aphrodite.callables;

import java.net.URL;

import org.jboss.set.aphrodite.Aphrodite;
import org.jboss.set.aphrodite.domain.PullRequest;
import org.jboss.set.aphrodite.spi.NotFoundException;

public class GetPullRequest extends AphroditeCallable<PullRequest> {

    private URL pullRequest;

    public GetPullRequest(Aphrodite aphrodite, URL pullRequestUrl) {
        super(aphrodite);
        this.pullRequest = pullRequestUrl;
    }

    @Override
    public PullRequest call() throws Exception {
        try {
            return aphrodite.getPullRequest(pullRequest);
        } catch (NotFoundException e) {
            throw new IllegalArgumentException("No such Pull Requests:" + pullRequest, e);
        }
    }

}
