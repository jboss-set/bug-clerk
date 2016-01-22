package org.jboss.jbossset.bugclerk.cli;

import org.jboss.jbossset.bugclerk.BugClerk;
import org.jboss.jbossset.bugclerk.aphrodite.AphroditeClient;
import org.jboss.set.aphrodite.config.IssueTrackerConfig;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

public final class CommandLineInterfaceUtils {

    private static final int INVALID_COMMAND_INPUT = 1;

    private CommandLineInterfaceUtils() {
    }

    public static <T extends CommonArguments> T extractParameters(T arguments, String... args) {
        JCommander jcommander = null;
        try {
            jcommander = new JCommander(arguments, args);
            jcommander.setProgramName(BugClerk.class.getSimpleName().toLowerCase());
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

    public static IssueTrackerConfig buildTrackerConfig(CommonArguments arguments, String trackerUrl) {
        return AphroditeClient.buildTrackerConfig(trackerUrl, arguments.getUsername(), arguments.getPassword(),
                arguments.getTrackerType());
    }

}
