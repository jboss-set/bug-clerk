package org.jboss.jbossset.bugclerk;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

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

public class LoadBugsIdsFromBZ {

    class Arguments {
        @Parameter(names = { "-h", "--bz-url" }, description="URL to BugZilla")
        private String authURL;
        public void setAuthURL(String bzUrl) { this.authURL = bzUrl; }
        public String getAuthURL() { return authURL; }

        @Parameter(names = {"-f", "--filter-url" }, description="URL to search filter")
        private String filterURL;
        public String getFilterURL() {
            return filterURL;
        }
        public void setFilterURL(String filterURL) {
            this.filterURL = filterURL;
        }

        @Parameter(names={ "-u", "--username"}, description= "Bugzilla username")
        private String username;
        public String getUsername() {
            return username;
        }
        public void setUsername(String username) {
            this.username = username;
        }

        @Parameter(names={ "-p", "--password"}, description = "Bugzilla password")
        private String password;
        public String getPassword() {
            return password;
        }
        public void setPassword(String password) {
            this.password = password;
        }
    }

    private static Arguments extractParameters(String[] args) {
        Arguments arguments = new LoadBugsIdsFromBZ().new Arguments();
        JCommander jcommander = null;
        try {
            jcommander = new JCommander(arguments, args);
            jcommander.setProgramName(LoadBugsIdsFromBZ.class.getName());
        } catch (ParameterException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
        return arguments;
    }

    public static void main(String[] args) throws ElementNotFoundException, FailingHttpStatusCodeException, MalformedURLException, IOException {
        Arguments arguments = extractParameters(args);
        System.out.print("Connection to BZ with URL " + arguments.getAuthURL() + " with username:" + arguments.getUsername() + " ... ");
        /* turn off annoying htmlunit warnings */
        java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(java.util.logging.Level.OFF);

        WebClient webClient = new WebClient();
        webClient.getCookieManager().setCookiesEnabled(true);

        final HtmlForm miniLoginForm = getFormById((HtmlPage) webClient.getPage(arguments.getAuthURL()), "mini_login_top");
        updateTextFieldInput("Bugzilla_login", arguments.getUsername(), miniLoginForm);
        updatePasswordFieldInput("Bugzilla_password",arguments.getPassword(), miniLoginForm);
        WebResponse response = miniLoginForm.getInputByName("GoAheadAndLogIn").click().getWebResponse();
        if ( response.getStatusCode() != 200 )
            throw new IllegalStateException("Auth faild on BZ:" + response.getContentAsString());
        System.out.println("Done.");

        System.out.print("Loading data from filter:" + arguments.getFilterURL() + " ... ");
        final TextPage csv = webClient.getPage(arguments.getFilterURL());
        if ( csv != null ) {
            runBugClerk(buildIdsCollection(csv), buildBzUrlPrefix(new URL(arguments.getFilterURL())));
        } else
            throw new IllegalStateException("Can't invoked filter" + " - got 'null' instead of content.");
    }

    private static void runBugClerk(Collection<String> ids, String urlPrefix) {
        BugClerk bc= new BugClerk();
        BugClerkArguments bcArgs = new BugClerkArguments();
        bcArgs.setUrlPrefix(urlPrefix);
        bcArgs.getIds().addAll(ids);
        bc.run(bcArgs);
    }

    private static Collection<String> buildIdsCollection(final TextPage csv) {
        Collection<String> ids = new ArrayList<String>(0);
        String[] content = csv.getContent().split("\n");
        // Remove CSV header line
        String[] idLines = Arrays.copyOfRange(content, 1,content.length);
        for ( String line : idLines )
            ids.add( validateId(line.substring(0, line.indexOf(','))));
        return ids;
    }

    private static String validateId(String id) {
        if ( id == null || "".equals(id))
            throw new IllegalArgumentException("BZ Id ");
        if ( Integer.valueOf(id) == null )
            throw new IllegalArgumentException("This is not a valid BZ id:" + id);
        return id;
    }

    private static String buildBzUrlPrefix(URL bzURL) {
        return bzURL.getProtocol() + "://" + bzURL.getHost() + "/show_bug.cgi?id=";
    }

    private static HtmlForm getFormById(final HtmlPage page, String formId) {
        for ( Object o : page.getForms() ) {
            HtmlForm form = (HtmlForm)o;
            if ( formId.equals(form.getId())) {
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
        final HtmlPasswordInput input =  (HtmlPasswordInput) form.getInputByName(textInputId);
        input.setValueAttribute(newValue);
    }


}
