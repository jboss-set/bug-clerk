package org.jboss.jbossset.bugclerk.aphrodite.callables;

import java.util.List;

import org.jboss.jbossset.bugclerk.utils.URLUtils;
import org.jboss.set.aphrodite.Aphrodite;
import org.jboss.set.aphrodite.domain.Issue;
import org.jboss.set.aphrodite.spi.NotFoundException;

public class SearchIssueByFilterTask extends AphroditeCallable<List<Issue>> {

    private final String filterUrl;

    public SearchIssueByFilterTask(Aphrodite aphrodite, String filterUrl) {
        super(aphrodite);
        this.filterUrl = filterUrl;
    }

    @Override
    public List<Issue> call() throws Exception {
        try {
            return aphrodite.searchIssuesByFilter(URLUtils.createURLFromString(filterUrl));
        } catch (NotFoundException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
