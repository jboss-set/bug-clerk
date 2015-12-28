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

import org.jboss.set.aphrodite.domain.Issue;

import com.beust.jcommander.IVariableArity;
import com.beust.jcommander.Parameter;

public class BugClerkArguments extends AbstractCommonArguments implements IVariableArity {

    @Parameter(description = "Issue IDs", variableArity = true, required = true)
    private final List<String> ids = new ArrayList<String>();

    private List<Issue> issues;

    public List<Issue> getIssues() {
        return issues;
    }

    public void setIssues(List<Issue> issues) {
        this.issues = issues;
    }

    public static BugClerkArguments validateArgs(BugClerkArguments arguments) {
        return arguments;
    }

    public List<String> getIds() {
        return ids;
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

    @Override
    public String toString() {
        return "BugClerkArguments [ids=" + ids + "," +  super.toString() + "]";
    }
}
