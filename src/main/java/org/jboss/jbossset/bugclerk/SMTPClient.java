package org.jboss.jbossset.bugclerk;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public final class SMTPClient {

    public static final String NO_EMAIL_CONFIGURATION_FOUND = "email-disabled";

    private static final String SMTP_HOST_FIELD = "mail.smtp.host"; //$NON-NLS-1$
    private static final String SMTP_HOST_NAME = SMTPClientConfiguration.getString("smtp.hostname");

    public static Session getSession() {
        Properties properties = System.getProperties();
        properties.setProperty(SMTP_HOST_FIELD, SMTP_HOST_NAME);
        return Session.getDefaultInstance(properties);
    }

    private void createAndSendEMail(String to, String from, String subject, String text) throws AddressException, MessagingException {
        MimeMessage message = new MimeMessage(SMTPClient.getSession());
        message.setFrom(new InternetAddress(from));
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
        message.setSubject(subject);
        message.setText(text);
        Transport.send(message);
    }

    public void sendEmail(String to, String from, String subject, String text) {
        try {
            if ( emailConfigured() )
                createAndSendEMail(to, from, subject, text);
            else
                System.err.println("No configuration for email found - skipping email report.");
        } catch (AddressException aex ) {
            throw new IllegalStateException(aex);
        } catch (MessagingException mex) {
            throw new IllegalStateException(mex);
        }
    }

    private boolean emailConfigured() {
        return ! SMTP_HOST_NAME.equals(NO_EMAIL_CONFIGURATION_FOUND);
    }

    public static void main(String[] args) {
        new SMTPClient().sendEmail(SMTPClientConfiguration.getString("smtp.recipient.email"), SMTPClientConfiguration.getString("smtp.sender.email"), "subject", "text"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
    }
}