package org.jboss.jbossset.bugclerk.cli;

import org.jboss.jbossset.bugclerk.BugClerk;
import org.jboss.jbossset.bugclerk.aphrodite.AphroditeClient;
import org.jboss.set.aphrodite.config.IssueTrackerConfig;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

public class AbstractCommandLineInterface {

    private static final int INVALID_COMMAND_INPUT = 1;

    protected static <T extends AbstractCommonArguments> T extractParameters(T arguments, String[] args) {
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

    protected static IssueTrackerConfig buildTrackerConfig(AbstractCommonArguments arguments, String trackerUrl) {
        return AphroditeClient.buildTrackerConfig(trackerUrl, arguments.getUsername(), arguments.getPassword(),
                arguments.getTrackerType());
    }

}
