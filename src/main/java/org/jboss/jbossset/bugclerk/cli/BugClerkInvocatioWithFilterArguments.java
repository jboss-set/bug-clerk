package org.jboss.jbossset.bugclerk.cli;

import com.beust.jcommander.Parameter;

public class BugClerkInvocatioWithFilterArguments extends AbstractCommonArguments {

    @Parameter(names = { "-H", "--bz-url" }, description = "URL to BugZilla")
    private String authURL;

    @Parameter(names = { "-f", "--filter-url" }, description = "URL to search filter")
    private String filterURL;

    @Parameter(names = { "-c", "--add-comment-on-bz" }, description = "add a comment to a BZ featuring violations, default is false", required = false)
    private boolean isCommentOnBZEnabled = false;

    @Parameter(names = { "-F", "--fail-on-violation" }, description = "exit program with status equals to number of violations", required = false)
    private boolean isFailOnViolation = false;

    @Parameter(names = { "-N", "--no-run" }, description = "Just fetch the result from filter and print the result - no calls to BugClerk itself.", required = false)
    private boolean isNoRun = false;

    @Parameter(names = { "-u", "--username" }, description = "username for bugzilla's connection - overload data from property file", required = false)
    private String username;

    @Parameter(names = { "-p", "--password" }, description = "password for bugzilla's connection - overload data from property file", required = false)
    private String password;

    public void setAuthURL(String bzUrl) {
        this.authURL = bzUrl;
    }

    public String getAuthURL() {
        return authURL;
    }

    public String getFilterURL() {
        return filterURL;
    }

    public void setFilterURL(String filterURL) {
        this.filterURL = filterURL;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isCommentOnBZEnabled() {
        return isCommentOnBZEnabled;
    }

    public void setCommentOnBZEnabled(boolean isCommentOnBZEnabled) {
        this.isCommentOnBZEnabled = isCommentOnBZEnabled;
    }

    public boolean isFailOnViolation() {
        return isFailOnViolation;
    }

    public void setFailOnViolation(boolean isFailOnViolation) {
        this.isFailOnViolation = isFailOnViolation;
    }

    public boolean isNoRun() {
        return isNoRun;
    }

    public void setNoRun(boolean isNoRun) {
        this.isNoRun = isNoRun;
    }
}