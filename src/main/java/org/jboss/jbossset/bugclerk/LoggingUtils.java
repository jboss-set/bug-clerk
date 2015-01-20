package org.jboss.jbossset.bugclerk;

import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class LoggingUtils {

    private LoggingUtils() {
    }

    private static final String BUG_CLERK_LOGGER_NAME = BugClerk.class.getCanonicalName();

    public static Logger getLogger() {
        return Logger.getLogger(BUG_CLERK_LOGGER_NAME);
    }

    public static void configureLogger(boolean debug) {
        Handler consoleHandler = new ConsoleHandler();
        Level level = getLevel(debug);
        consoleHandler.setLevel(level);
        Logger.getLogger(BUG_CLERK_LOGGER_NAME).addHandler(consoleHandler);
        Logger.getLogger(BUG_CLERK_LOGGER_NAME).setLevel(level);
    }

    private static Level getLevel(boolean debug) {
        return (debug ? Level.FINE : Level.INFO);
    }
}