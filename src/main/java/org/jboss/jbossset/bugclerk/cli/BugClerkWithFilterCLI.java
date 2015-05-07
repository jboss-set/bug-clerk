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

import static org.jboss.jbossset.bugclerk.utils.StringUtils.emptyOrNull;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Properties;

import org.jboss.jbossset.bugclerk.BugClerk;
import org.jboss.jbossset.bugclerk.bugzilla.BugzillaClient;
import org.jboss.jbossset.bugclerk.bugzilla.BugzillaDrone;
import org.jboss.jbossset.bugclerk.utils.PropertiesUtils;
import org.jboss.jbossset.bugclerk.utils.URLUtils;

public class BugClerkWithFilterCLI extends AbstractCommandLineInterface {

    public static void main(String[] args) throws MalformedURLException {
        BugClerkInvocatioWithFilterArguments arguments = loadUsernamePassword(extractParameters(new BugClerkInvocatioWithFilterArguments(),args));
        System.out.print("Connection to BZ with URL " + arguments.getAuthURL() + " with username:" + arguments.getUsername()
                + " ... ");
        BugzillaDrone drone = new BugzillaDrone(arguments);
        drone.bugzillaLogin();
        System.out.println("Done.");

        System.out.print("Loading data from filter:" + arguments.getFilterURL() + " ... ");
        final Collection<String> ids = drone.retrievePayload();
        if ( arguments.isNoRun() ) return;

        if ( !ids.isEmpty()) {
            endProgram(arguments, runBugClerk(ids, URLUtils.buildBzUrlPrefix(new URL(arguments.getFilterURL()))));
        } else
            throw new IllegalStateException("Can't invoked filter" + " - got 'null' instead of content.");
    }

    private static BugClerkInvocatioWithFilterArguments loadUsernamePassword(BugClerkInvocatioWithFilterArguments arguments) {
        Properties prop = PropertiesUtils.loadPropertiesFile(BugzillaClient.CONFIGURATION_FILENAME);
        if (emptyOrNull(arguments.getPassword()))
            arguments.setUsername(prop.getProperty("bugzilla.login"));
        if (emptyOrNull(arguments.getPassword()))
            arguments.setPassword(prop.getProperty("bugzilla.password"));
        return arguments;
    }

    private static void endProgram(BugClerkInvocatioWithFilterArguments arguments, int nbViolation) {
        int status = 0;
        if (arguments.isFailOnViolation())
            status = nbViolation;

        // Jenkins and/or Maven deemed that calling exit, even with 0 value, is a failure, hence this workaround :(
        if (status != 0)
            System.exit(status);
    }

    private static int runBugClerk(Collection<String> ids, String urlPrefix) {
        BugClerk bc = new BugClerk();
        BugClerkArguments bcArgs = new BugClerkArguments();
        bcArgs.setUrlPrefix(urlPrefix);
        bcArgs.getIds().addAll(ids);
        return bc.runAndReturnsViolations(bcArgs);
    }
}