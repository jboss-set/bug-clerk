package org.jboss.jbossset.bugclerk.bugzilla;

import java.io.IOException;

import org.jboss.jbossset.bugclerk.cli.BugClerkInvocatioWithFilterArguments;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
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
    private BugClerkInvocatioWithFilterArguments arguments;

    public BugzillaDrone(BugClerkInvocatioWithFilterArguments arguments) {
        this.arguments = arguments;
        /* turn off annoying htmlunit warnings */
        java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(java.util.logging.Level.OFF);

        webClient = new WebClient();
        webClient.getCookieManager().setCookiesEnabled(true);
    }

    public void bugzillaLogin() {
        try {
        final HtmlForm miniLoginForm = getFormById((HtmlPage) webClient.getPage(arguments.getAuthURL()), "mini_login_top");
        updateTextFieldInput("Bugzilla_login", arguments.getUsername(), miniLoginForm);
        updatePasswordFieldInput("Bugzilla_password", arguments.getPassword(), miniLoginForm);
        WebResponse response = miniLoginForm.getInputByName("GoAheadAndLogIn").click().getWebResponse();
        if (response.getStatusCode() != 200)
            throw new IllegalStateException("Auth faild on BZ:" + response.getContentAsString());
        } catch (IOException e ) {
            throw new IllegalStateException(e);
        }
    }


    public TextPage retrievePayload() {
        TextPage csv;
        try {
            csv = webClient.getPage(arguments.getFilterURL());
        } catch (FailingHttpStatusCodeException | IOException e) {
            throw new IllegalStateException(e);
        }
        webClient.closeAllWindows();
        return csv;
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

