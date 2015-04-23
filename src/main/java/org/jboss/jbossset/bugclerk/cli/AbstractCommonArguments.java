package org.jboss.jbossset.bugclerk.cli;

import com.beust.jcommander.Parameter;

public abstract class AbstractCommonArguments {

    @Parameter(names = { "-h", "--help" }, description = "print help text", required = false)
    private boolean help = false;


    @Parameter(names = { "-d", "--debug" }, description = "debug mode", required = false)
    private boolean debug = false;


    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public boolean isHelp() {
        return help;
    }

    public void setHelp(boolean help) {
        this.help = help;
    }

}
