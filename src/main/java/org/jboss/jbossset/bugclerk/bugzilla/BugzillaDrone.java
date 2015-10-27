package org.jboss.jbossset.bugclerk.bugzilla;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.TextPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;

/**
 * Encompass automation of interaction with Bugzilla Server
 *
 * @author Romain Pelisse - belaran@redhat.com
 *
 */
public class BugzillaDrone {

    private WebClient webClient;

    private final String authUrl;
    private final String filterUrl;
    private final String username;
    private final String password;

    private final boolean isNoRun;

    private final boolean debug = false;
    private static final String LOGGER_NAME = "com.gargoylesoftware";

    private void init() {
        /* turn off annoying htmlunit warnings */
        java.util.logging.Logger.getLogger(LOGGER_NAME).setLevel(java.util.logging.Level.OFF);

        webClient = new WebClient();
        webClient.getCookieManager().setCookiesEnabled(true);
    }

    public BugzillaDrone(String authURL, String filterURL, String username, String password, boolean isNoRun) {
        this.authUrl = authURL;
        this.filterUrl = filterURL;
        this.username = username;
        this.password = password;
        this.isNoRun = isNoRun;
        init();
    }

    public void bugzillaLogin() {
        try {
            final HtmlForm miniLoginForm = getFormById((HtmlPage) webClient.getPage(authUrl), "mini_login_top");
            updateTextFieldInput("Bugzilla_login", username, miniLoginForm);
            updatePasswordFieldInput("Bugzilla_password", password, miniLoginForm);
            WebResponse response = miniLoginForm.getInputByName("GoAheadAndLogIn").click().getWebResponse();
            printPage(response);
            if (response.getStatusCode() != 200)
                throw new IllegalStateException("Auth failed on BZ:" + response.getContentAsString());
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private Collection<String> buildIdsCollection(final TextPage csv) {
        Collection<String> ids = new ArrayList<String>(0);
        if (csv != null) {
            String[] content = csv.getContent().split("\n");
            // Remove CSV header line
            String[] idLines = Arrays.copyOfRange(content, 1, content.length);
            for (String line : idLines) {
                if (isNoRun)
                    System.err.println(line);
                ids.add(validateId(line.substring(0, line.indexOf(','))));
            }
        }
        return ids;
    }

    private static String validateId(String id) {
        if (id == null || "".equals(id))
            throw new IllegalArgumentException("BZ Id ");
        if (Integer.valueOf(id) == null)
            throw new IllegalArgumentException("This is not a valid BZ id:" + id);
        return id;
    }

    private void printPage(WebResponse response) {
        if ( debug )
            System.err.println(response.getContentAsString());
    }

    public Collection<String> retrievePayload() {
        TextPage csv;
        try {
            Page page  = webClient.getPage(filterUrl);
            printPage(page.getWebResponse());
            if ( page instanceof TextPage )
                csv = (TextPage) page;
            else {
                throw new IllegalStateException(
                        "Data loaded from bugzilla instance is not compatibile with CSV type. Most likely filter URL is simply missing the 'ctype=csv' type. Page type returned is:" + page.getClass());
            }
        } catch (FailingHttpStatusCodeException | IOException e) {
            throw new IllegalStateException(e);
        } catch (java.lang.ClassCastException e) {
            throw new IllegalStateException(
                    "Data loaded from bugzilla instance is not compatibile with CSV type. Most likely filter URL is simply missing the 'ctype=csv' type.",
                    e);
        }
        webClient.closeAllWindows();
        return buildIdsCollection(csv);
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

    private static void updateTextFieldInput(String textInputId, String newValue, HtmlForm form) {
        final HtmlInput input = form.getInputByName(textInputId);
        input.setValueAttribute(newValue);
    }

    private static void updatePasswordFieldInput(String textInputId, String newValue, HtmlForm form) {
        final HtmlPasswordInput input = (HtmlPasswordInput) form.getInputByName(textInputId);
        input.setValueAttribute(newValue);
    }

}
