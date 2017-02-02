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

import java.net.MalformedURLException;
import java.util.List;

import org.jboss.jbossset.bugclerk.BugClerk;
import org.jboss.jbossset.bugclerk.aphrodite.AphroditeClient;
import org.jboss.set.aphrodite.domain.Issue;
import org.jboss.set.aphrodite.spi.AphroditeException;
import org.jboss.set.aphrodite.spi.NotFoundException;

public final class BugClerkWithFilterCLI {

    private BugClerkWithFilterCLI() {
    }

    public static void main(String[] args) throws MalformedURLException, AphroditeException, NotFoundException {
        BugClerkInvocatioWithFilterArguments arguments = CommandLineInterfaceUtils.extractParameters(
                new BugClerkInvocatioWithFilterArguments(), args);
        AphroditeClient client = new AphroditeClient();
        final List<Issue> issues = client.retrievePayload(arguments.getFilterURL());

        if (!issues.isEmpty())
            endProgram(arguments, runBugClerk(issues, client, arguments));
        client.close();
    }

    private static void endProgram(BugClerkInvocatioWithFilterArguments arguments, int nbViolation) {
        int status = 0;
        if (arguments.isFailOnViolation())
            status = nbViolation;

        // Jenkins and/or Maven deemed that invoking exit, even with 0 value, is a failure, hence this workaround :(
        if (status != 0)
            System.exit(status);
    }

    private static int runBugClerk(List<Issue> issues, AphroditeClient aphrodite, BugClerkInvocatioWithFilterArguments arguments) {
        BugClerk bc = new BugClerk(aphrodite);
        BugClerkArguments bcArgs = buildArgumentsFrom(arguments);
        bcArgs.setIssues(issues);
        return bc.runAndReturnsViolations(bcArgs);
    }

    private static BugClerkArguments buildArgumentsFrom(BugClerkInvocatioWithFilterArguments arguments) {
        BugClerkArguments bcArgs = new BugClerkArguments();
        bcArgs.setReportToBz(arguments.isReportToBz());
        bcArgs.setXmlReportFilename(arguments.getXmlReportFilename());
        bcArgs.setHtmlReportFilename(arguments.getHtmlReportFilename());
        return bcArgs;
    }
}