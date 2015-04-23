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

import com.beust.jcommander.IVariableArity;
import com.beust.jcommander.Parameter;

public class BugClerkArguments extends AbstractCommonArguments implements IVariableArity {

    @Parameter(names = { "-u", "--url-prefix" }, description = "URL prefix (before the issue ID)", required = true)
    private String urlPrefix;

    @Parameter(description = "Issue IDs", variableArity = true)
    private final List<String> ids = new ArrayList<String>();

    @Parameter(names = { "-c", "--comment-on-bz"}, description = "add a comment to a BZ featuring violations, default is false",required = false)
    private boolean reportToBz = false;

    @Parameter(names = { "-m", "--mail-report"}, description = "sent a report by mail, default is false",required = false)
    private boolean mailReport = false;

    public static BugClerkArguments validateArgs(BugClerkArguments arguments) {
        return arguments;
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
