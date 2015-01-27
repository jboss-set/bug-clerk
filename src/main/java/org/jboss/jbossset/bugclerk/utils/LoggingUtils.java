package org.jboss.jbossset.bugclerk.utils;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.jbossset.bugclerk.BugClerk;

public final class LoggingUtils {

    private LoggingUtils() {
    }

    private static final String BUG_CLERK_LOGGER_NAME = BugClerk.class.getCanonicalName();

    public static Logger getLogger() {
        return Logger.getLogger(BUG_CLERK_LOGGER_NAME);
    }

    public static void configureLogger(boolean debug) {
        Logger.getLogger(BUG_CLERK_LOGGER_NAME).setLevel(getLevel(debug));
    }

    private static Level getLevel(boolean debug) {
        return (debug ? Level.FINE : Level.INFO);
    }
}