package org.jboss.jbossset.bugclerk.cli;

import com.beust.jcommander.Parameter;

public class BugClerkInvocatioWithFilterArguments extends AbstractCommonArguments {

    @Parameter(names = { "-f", "--filter-url" }, description = "URL to search filter")
    private String filterURL;

    @Parameter(names = { "-c", "--add-comment-on-bz" }, description = "add a comment to a BZ featuring violations, default is false", required = false)
    private boolean isCommentOnBZEnabled = false;

    @Parameter(names = { "-F", "--fail-on-violation" }, description = "exit program with status equals to number of violations", required = false)
    private boolean isFailOnViolation = false;

    public String getFilterURL() {
        return filterURL;
    }

    public void setFilterURL(String filterURL) {
        this.filterURL = filterURL;
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
}