package org.jboss.jbossset.bugclerk.aphrodite.callables;

import java.util.List;
import java.util.stream.Collectors;

import org.jboss.jbossset.bugclerk.utils.URLUtils;
import org.jboss.set.aphrodite.Aphrodite;
import org.jboss.set.aphrodite.domain.Issue;

public class LoadIssuesTask extends AphroditeCallable<List<Issue>> {

    private final List<String> ids;

    public LoadIssuesTask(Aphrodite aphrodite, List<String> ids) {
        super(aphrodite);
        this.ids = ids;
    }

    @Override
    public List<Issue> call() throws Exception {
        return aphrodite.getIssues(ids.parallelStream().map(id -> URLUtils.createURLFromString(id))
                .collect(Collectors.toList()));
    }
}