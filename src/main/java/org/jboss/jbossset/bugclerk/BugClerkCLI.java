package org.jboss.jbossset.bugclerk;

public class BugClerkCLI {

    private static final int PROGRAM_THROWN_EXCEPTION = 3;

    public static void main(String[] args) {
        try {
            new BugClerk().run(BugClerkArguments.validateArgs(BugClerkArguments.extractParameters(args)));
        } catch (Throwable t) {
            System.out.println(t.getMessage());
            if (t.getCause() != null)
                System.out.println(t.getCause().getMessage());
            System.exit(PROGRAM_THROWN_EXCEPTION);
        }
    }

}
