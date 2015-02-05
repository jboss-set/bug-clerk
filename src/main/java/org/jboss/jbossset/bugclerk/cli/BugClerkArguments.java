/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2015, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jboss.jbossset.bugclerk.cli;

import java.util.ArrayList;
import java.util.List;

import org.jboss.jbossset.bugclerk.BugClerk;

import com.beust.jcommander.IVariableArity;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;

public class BugClerkArguments implements IVariableArity {

    @Parameter(names = { "-u", "--url-prefix" }, description = "URL prefix (before the issue ID)", required = true)
    private String urlPrefix;

    @Parameter(description = "Issue IDs", variableArity = true)
    private final List<String> ids = new ArrayList<String>();

    @Parameter(names = { "-h", "--help" }, description = "print help text", required = false)
    private boolean help = false;

    @Parameter(names = { "-c", "--comment-on-bz"}, description = "add a comment to a BZ featuring violations, default is false",required = false)
    private boolean reportToBz = false;

    @Parameter(names = { "-m", "--mail-report"}, description = "sent a report by mail, default is false",required = false)
    private boolean mailReport = false;

    @Parameter(names = { "-d", "--debug" }, description = "debug mode", required = false)
    private boolean debug = false;

    private static final String PROG_NAME = BugClerk.class.getSimpleName().toLowerCase();

    private static final int INVALID_COMMAND_INPUT = 1;

    public static BugClerkArguments extractParameters(String[] args) {
        BugClerkArguments arguments = new BugClerkArguments();
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

    public static BugClerkArguments validateArgs(BugClerkArguments arguments) {
        return arguments;
    }

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

    public String getUrlPrefix() {
        return urlPrefix;
    }

    public void setUrlPrefix(String urlPrefix) {
        this.urlPrefix = urlPrefix;
    }

    public List<String> getIds() {
        return ids;
    }

    public boolean isReportToBz() {
        return reportToBz;
    }

    public void setReportToBz(boolean reportToBz) {
        this.reportToBz = reportToBz;
    }

    public boolean isMailReport() {
        return mailReport;
    }

    public void setMailReport(boolean mailReport) {
        this.mailReport = mailReport;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (help ? 1231 : 1237);
        result = prime * result + ((urlPrefix == null) ? 0 : urlPrefix.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        BugClerkArguments other = (BugClerkArguments) obj;
        if (help != other.help)
            return false;
        if (urlPrefix == null) {
            if (other.urlPrefix != null)
                return false;
        } else if (!urlPrefix.equals(other.urlPrefix))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Arguments [issueId=" + urlPrefix + ", help=" + help + "]";
    }

    @Override
    public int processVariableArity(String optionName, String[] options) {
        if ("-i".equals(optionName) || "--ids".equals(optionName)) {
            for (String id : options) {
                this.ids.add(id);
            }
            return this.ids.size();
        }
        return 0;
    }

}
