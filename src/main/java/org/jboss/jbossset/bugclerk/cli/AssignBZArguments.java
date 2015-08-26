package org.jboss.jbossset.bugclerk.cli;

import com.beust.jcommander.Parameter;

public class AssignBZArguments {

    @Parameter(names = { "-u", "--username" }, description = "Bugzilla username", required = true)
    private String username;

    @Parameter(names = { "-p", "--password" }, description = "BugZilla password", required = true)
    private String password;

    @Parameter(names = { "-i", "--bug-id" }, description = "bug id", required = true)
    private String bugId;

    @Parameter(names = { "-e", "--estimate" }, description = "estimate", required = false)
    private double estimate = 24.0;

    @Parameter(names = {"-a", "--assigned-to" }, description = "", required = true)
    private String assignedTo;

    @Parameter(names = {"-c", "--comment" }, description = "", required = false)
    private String comment = "I'll take a look at this issue asap.";

    @Parameter(names = { "-?", "--help" }, description = "print help text", required = false)
    private boolean help = false;

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getBugId() {
        return bugId;
    }

    public double getEstimate() {
        return estimate;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public String getComment() {
        return comment;
    }

    public boolean isHelp() {
        return help;
    }
}
