package org.jboss.jbossset.bugclerk.cli;

import com.beust.jcommander.Parameter;

public class BugClerkInvocatioWithFilterArguments extends CommonArguments {

    @Parameter(names = { "-f", "--filter-url" }, description = "Tracker filter URL")
    private String filterURL;

    public String getFilterURL() {
        return filterURL;
    }

    public void setFilterURL(String filterURL) {
        this.filterURL = filterURL;
    }

}