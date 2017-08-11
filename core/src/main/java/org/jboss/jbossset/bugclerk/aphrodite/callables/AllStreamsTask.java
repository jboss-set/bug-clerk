package org.jboss.jbossset.bugclerk.aphrodite.callables;

import java.util.List;

import org.jboss.set.aphrodite.Aphrodite;
import org.jboss.set.aphrodite.domain.Stream;

public class AllStreamsTask extends AphroditeCallable<List<Stream>> {

    public AllStreamsTask(Aphrodite aphrodite) {
        super(aphrodite);
    }

    @Override
    public List<Stream> call() throws Exception {
        return aphrodite.getAllStreams();
    }

}
