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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Properties;

import org.jboss.jbossset.bugclerk.BugClerk;
import org.jboss.jbossset.bugclerk.bugzilla.BugzillaClient;
import org.jboss.jbossset.bugclerk.bugzilla.BugzillaDrone;
import org.jboss.pull.shared.Util;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import com.gargoylesoftware.htmlunit.TextPage;

public class VerifyBZsFromFilter {

    public static void main(String[] args) throws MalformedURLException {
        BugClerkInvocatioWithFilterArguments arguments = loadUsernamePassword(extractParameters(args));
        System.out.print("Connection to BZ with URL " + arguments.getAuthURL() + " with username:" + arguments.getUsername()
                + " ... ");
        BugzillaDrone drone = new BugzillaDrone(arguments);
        drone.bugzillaLogin();
        System.out.println("Done.");

        System.out.print("Loading data from filter:" + arguments.getFilterURL() + " ... ");
        final TextPage csv = drone.retrievePayload();
        if (csv != null) {
            endProgram(arguments, runBugClerk(buildIdsCollection(csv), buildBzUrlPrefix(new URL(arguments.getFilterURL()))));
        } else
            throw new IllegalStateException("Can't invoked filter" + " - got 'null' instead of content.");
    }

    private static BugClerkInvocatioWithFilterArguments extractParameters(String[] args) {
        BugClerkInvocatioWithFilterArguments arguments = new BugClerkInvocatioWithFilterArguments();
        JCommander jcommander = null;
        try {
            jcommander = new JCommander(arguments, args);
            jcommander.setProgramName(VerifyBZsFromFilter.class.getName());
        } catch (ParameterException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
        return arguments;
    }

    private static Properties loadPropertiesFile(String filename) {
        try {
            return Util.loadProperties(filename, filename);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private static BugClerkInvocatioWithFilterArguments loadUsernamePassword(BugClerkInvocatioWithFilterArguments arguments) {
        Properties prop = loadPropertiesFile(BugzillaClient.CONFIGURATION_FILENAME);
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

    private static Collection<String> buildIdsCollection(final TextPage csv) {
        Collection<String> ids = new ArrayList<String>(0);
        String[] content = csv.getContent().split("\n");
        // Remove CSV header line
        String[] idLines = Arrays.copyOfRange(content, 1, content.length);
        for (String line : idLines)
            ids.add(validateId(line.substring(0, line.indexOf(','))));
        return ids;
    }

    private static String validateId(String id) {
        if (id == null || "".equals(id))
            throw new IllegalArgumentException("BZ Id ");
        if (Integer.valueOf(id) == null)
            throw new IllegalArgumentException("This is not a valid BZ id:" + id);
        return id;
    }

    private static String buildBzUrlPrefix(URL bzURL) {
        return bzURL.getProtocol() + "://" + bzURL.getHost() + "/show_bug.cgi?id=";
    }
}