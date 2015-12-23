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

import org.jboss.jbossset.bugclerk.BugClerk;
import org.jboss.jbossset.bugclerk.aphrodite.AphroditeClient;
import org.jboss.jbossset.bugclerk.aphrodite.AphroditeParameters;

public class BugClerkCLI extends AbstractCommandLineInterface {

    private static final int PROGRAM_THROWN_EXCEPTION = 3;

    public static void main(String[] args) {
        try {
            BugClerkArguments arguments = extractParameters(new BugClerkArguments(), args);
            new BugClerk(new AphroditeClient(buildAphroditeParameters(arguments))).runAndReturnsViolations(BugClerkArguments
                    .validateArgs(arguments));
        } catch (Throwable t) {
            System.out.println(t.getMessage());
            if (t.getCause() != null)
                System.out.println(t.getCause().getMessage());
            System.exit(PROGRAM_THROWN_EXCEPTION);
        }
    }

    protected static AphroditeParameters buildAphroditeParameters(BugClerkArguments arguments) {
        return new AphroditeParameters(arguments.getUrlPrefix(), arguments.getUrlPrefix(), arguments.getPassword());
    }
}
