package org.jboss.jbossset.bugclerk.smtp;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class SMTPClientConfiguration {

    private static final String BUNDLE_NAME = "org.jboss.jbossset.bugclerk.smtp.config";

    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

    private SMTPClientConfiguration() {
    }

    public static String getString(String key) {
        try {
            return RESOURCE_BUNDLE.getString(key);
        } catch (MissingResourceException e) {
            return SMTPClient.NO_EMAIL_CONFIGURATION_FOUND;
        }
    }
}
