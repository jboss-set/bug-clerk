package org.jboss.jbossset.bugclerk.smtp;

import static org.junit.Assert.fail;

import java.util.Properties;

import org.junit.Test;
import org.junit.Ignore;

@Ignore
public class SMTPClientUnitTest {

    @Test
    public void main() {
        try {
            Properties smtpProperties = SMTPClient.loadSMTPProperties();
            SMTPClient cl = new SMTPClient();
            cl.sendEmail(smtpProperties.getProperty("smtp.recipient.email"), smtpProperties.getProperty("smtp.sender.email"),
                    "subject", "");
            cl.sendEmail(smtpProperties.getProperty("smtp.recipient.email"), smtpProperties.getProperty("smtp.sender.email"),
                    "subject", null);
            cl.sendEmail(smtpProperties.getProperty("smtp.recipient.email"), smtpProperties.getProperty("smtp.sender.email"),
                    "subject", "text");
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

}
