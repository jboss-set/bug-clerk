package org.jboss.jbossset.bugclerk;

import java.util.Collection;

import org.jboss.pull.shared.connectors.bugzilla.Bug;
import org.jboss.pull.shared.connectors.bugzilla.Comment;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

public class BugClerk {

    private static final String PROG_NAME = BugClerk.class.getSimpleName().toLowerCase();

    private static final int INVALID_COMMAND_INPUT = 1;
    private static final int PROGRAM_THROWN_EXCEPTION = 3;

    private static final String KIE_SESSION = "BzCheck";
    private static final RuleEngine ruleEngine = new RuleEngine(KIE_SESSION);

    private static void consolePrint(String string, boolean printCarriageReturn) {
        if (printCarriageReturn)
            System.out.println(string); // NOPMD
        else
            System.out.print(string); // NOPMD
    }

    private static void consolePrint(String string) {
        consolePrint(string, true);
    }

    public static void main(String[] args) {
        runWithCatch(args);
    }

    public static void runWithCatch(String[] args) {
        try {
            run(args);
        } catch (Throwable t) {
            consolePrint(t.getMessage());
            if (t.getCause() != null)
                consolePrint(t.getCause().getMessage());
            System.exit(PROGRAM_THROWN_EXCEPTION);
        }
    }

    private static Arguments extractParameters(String[] args) {
        Arguments arguments = new Arguments();
        JCommander jcommander = null;
        try {
            jcommander = new JCommander(arguments, args);
            jcommander.setProgramName(PROG_NAME);
            if (arguments.isHelp()) {
                jcommander.usage();
                System.exit(0);
            }

        } catch (ParameterException e) {
            System.out.println(e.getMessage());
            System.exit(INVALID_COMMAND_INPUT);
        }
        return arguments;
    }

    private static Arguments validateArgs(Arguments arguments) {
        return arguments;
    }

    private static void reportViolations(Collection<Violation> violations) {
        if ( ! violations.isEmpty() ) {
            for ( Violation violation : violations )
                System.out.println(violation);
        }
    }

    public static void run(String[] args) {
        long startTime = System.nanoTime();
        Arguments arguments = validateArgs(BugClerk.extractParameters(args));
        LoggingUtils.configureLogger(arguments.isDebug());

        Bug issue = BzUtils.loadBzFromUrl(URLUtils.createURLFromString(arguments.getIssueId()));
        Collection<Comment> comments = BzUtils.loadCommentForBug(issue);
        SystemUtils.printOnError("Retrieved BZ information took:" + SystemUtils.timeSpentInSecondsSince(startTime) + "s.");
        startTime = System.nanoTime();

        Candidate candidate = new Candidate(issue);

        Object[] facts = { comments, candidate };

        reportViolations(ruleEngine.processBugEntry(facts));
        SystemUtils.printOnError("Analysis took:" + SystemUtils.timeSpentInSecondsSince(startTime) + "s.");
        ruleEngine.shutdownRuleEngine();
    }
}
