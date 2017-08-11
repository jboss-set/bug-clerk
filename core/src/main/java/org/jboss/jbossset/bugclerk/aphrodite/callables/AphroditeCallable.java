package org.jboss.jbossset.bugclerk.aphrodite.callables;

import java.util.concurrent.Callable;

import org.jboss.set.aphrodite.Aphrodite;

public abstract class AphroditeCallable<T> implements Callable<T> {

    protected final Aphrodite aphrodite;

    public AphroditeCallable(Aphrodite aphrodite) {
        this.aphrodite = aphrodite;
    }
}
