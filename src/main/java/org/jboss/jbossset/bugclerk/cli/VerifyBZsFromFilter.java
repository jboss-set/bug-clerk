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

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Properties;

import org.jboss.jbossset.bugclerk.BugClerk;
import org.jboss.jbossset.bugclerk.bugzilla.BugzillaClient;
import org.jboss.pull.shared.Util;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.TextPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;

public class VerifyBZsFromFilter {

    class Arguments {
        @Parameter(names = { "-h", "--bz-url" }, description = "URL to BugZilla")
        private String authURL;

        @Parameter(names = { "-f", "--filter-url" }, description = "URL to search filter")
        private String filterURL;

        @Parameter(names = { "-c", "--add-comment-on-bz"} , description = "add a comment to a BZ featuring violations, default is false", required = false)
        private boolean isCommentOnBZEnabled = false;

        @Parameter(names = { "-v", "--fail-on-violation"} , description = "exit program with status equals to number of violations", required = false)
        private boolean isFailOnViolation = false;

        @Parameter(names = { "-u", "--username"} , description = "username for bugzilla's connection - overload data from property file", required = false)
        private String username;

        @Parameter(names = { "-p", "--password"} , description = "password for bugzilla's connection - overload data from property file", required = false)
        private String password;

        public void setAuthURL(String bzUrl) {
            this.authURL = bzUrl;
        }

        public String getAuthURL() {
            return authURL;
        }

        public String getFilterURL() {
            return filterURL;
        }

        public void setFilterURL(String filterURL) {
            this.filterURL = filterURL;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public boolean isCommentOnBZEnabled() {
            return isCommentOnBZEnabled;
        }

        public void setCommentOnBZEnabled(boolean isCommentOnBZEnabled) {
            this.isCommentOnBZEnabled = isCommentOnBZEnabled;
        }
    }

    private static Arguments extractParameters(String[] args) {
        Arguments arguments = new VerifyBZsFromFilter().new Arguments();
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

    private static Arguments loadUsernamePassword(Arguments arguments) {

        Properties prop;
        try {
            prop = Util.loadProperties(BugzillaClient.CONFIGURATION_FILENAME, BugzillaClient.CONFIGURATION_FILENAME);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        arguments.setUsername(prop.getProperty("bugzilla.login"));
        arguments.setPassword(prop.getProperty("bugzilla.password"));
        return arguments;
    }

    public static void main(String[] args) throws ElementNotFoundException, FailingHttpStatusCodeException, IOException {
        Arguments arguments = loadUsernamePassword(extractParameters(args));
        System.out.print("Connection to BZ with URL " + arguments.getAuthURL() + " with username:" + arguments.getUsername()
                + " ... ");
        /* turn off annoying htmlunit warnings */
        java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(java.util.logging.Level.OFF);

        WebClient webClient = new WebClient();
        webClient.getCookieManager().setCookiesEnabled(true);

        final HtmlForm miniLoginForm = getFormById((HtmlPage) webClient.getPage(arguments.getAuthURL()), "mini_login_top");
        updateTextFieldInput("Bugzilla_login", arguments.getUsername(), miniLoginForm);
        updatePasswordFieldInput("Bugzilla_password", arguments.getPassword(), miniLoginForm);
        WebResponse response = miniLoginForm.getInputByName("GoAheadAndLogIn").click().getWebResponse();
        if (response.getStatusCode() != 200)
            throw new IllegalStateException("Auth faild on BZ:" + response.getContentAsString());
        System.out.println("Done.");

        System.out.print("Loading data from filter:" + arguments.getFilterURL() + " ... ");
        final TextPage csv = webClient.getPage(arguments.getFilterURL());
        if (csv != null) {
            System.exit(runBugClerk(buildIdsCollection(csv), buildBzUrlPrefix(new URL(arguments.getFilterURL()))));
        } else
            throw new IllegalStateException("Can't invoked filter" + " - got 'null' instead of content.");
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

    private static HtmlForm getFormById(final HtmlPage page, String formId) {
        for (Object o : page.getForms()) {
            HtmlForm form = (HtmlForm) o;
            if (formId.equals(form.getId())) {
                return form;
            }
        }
        throw new IllegalArgumentException("No Form with ID " + formId);
    }

    public static void updateTextFieldInput(String textInputId, String newValue, HtmlForm form) {
        final HtmlInput input = form.getInputByName(textInputId);
        input.setValueAttribute(newValue);
    }

    public static void updatePasswordFieldInput(String textInputId, String newValue, HtmlForm form) {
        final HtmlPasswordInput input = (HtmlPasswordInput) form.getInputByName(textInputId);
        input.setValueAttribute(newValue);
    }

}
