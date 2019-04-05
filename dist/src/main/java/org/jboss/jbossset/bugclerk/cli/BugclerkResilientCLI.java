package org.jboss.jbossset.bugclerk.cli;

import java.util.concurrent.TimeUnit;

public final class BugclerkResilientCLI {

    private static final int WAIT_TIME_BETWEEN_RUN_IN_SECONDS = 10;
    private static final int NB_ATTTEMPT_TO_RUN_BUGCLERK_BEFORE_FAILING = 3;

    public static void main(String[] args) {
        int nbAtemptToRunBugclerkLeft = NB_ATTTEMPT_TO_RUN_BUGCLERK_BEFORE_FAILING;
        do {
            try {
                BugClerkWithFilterCLI.main(args);
            } catch ( Exception e ) {
                System.err.println("Bugclerk could not run due to following error(s):" + e.getLocalizedMessage());
                System.err.println("Wait for " + "s and try again (" + nbAtemptToRunBugclerkLeft-- + " attempts remaining)");
                pause(WAIT_TIME_BETWEEN_RUN_IN_SECONDS);
            }
        } while ( nbAtemptToRunBugclerkLeft > 0 );

    }

    private static void pause(int nbSeconds) {
        try {
            TimeUnit.SECONDS.sleep(nbSeconds);
        } catch (InterruptedException e) {
            System.err.println("Failed to pause system:" + e.getLocalizedMessage());
        }
    }

}
