package org.jboss.jbossset.bugclerk.smtp;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.jboss.jbossset.bugclerk.bugzilla.BugzillaClient;
import org.jboss.pull.shared.Util;

public final class SMTPClient {

    public static final String NO_EMAIL_CONFIGURATION_FOUND = "email-disabled";

    private static final String SMTP_HOST_FIELD = "mail.smtp.host";

    private String smtpHostname;

    public SMTPClient() {
        Properties smtpProperties = loadSMTPProperties();
        smtpHostname = smtpProperties.getProperty(SMTP_HOST_FIELD);
    }

    public Session getSession() {
        Properties properties = System.getProperties();
        properties.setProperty(SMTP_HOST_FIELD, smtpHostname);
        return Session.getDefaultInstance(properties);
    }

    private static Properties loadSMTPProperties() {
        try {
            return Util.loadProperties(BugzillaClient.CONFIGURATION_FILENAME, BugzillaClient.CONFIGURATION_FILENAME);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private void createAndSendEMail(String to, String from, String subject, String text) throws AddressException, MessagingException {
        MimeMessage message = new MimeMessage(getSession());
        message.setFrom(new InternetAddress(from));
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
        message.setSubject(subject);
        message.setText(text);
        Transport.send(message);
    }

    public void sendEmail(String to, String from, String subject, String text) {
        try {
            if ( emailConfigured() && ! isEmailEmpty(text))
                createAndSendEMail(to, from, subject, text);
            else
                System.err.println("No configuration for email found or empty mail - skipping email.");
        } catch (AddressException aex ) {
            throw new IllegalStateException(aex);
        } catch (MessagingException mex) {
            throw new IllegalStateException(mex);
        }
    }

    private boolean isEmailEmpty(String text) {
        return (text == null || "".equals(text) );
    }

    private boolean emailConfigured() {
        return ! NO_EMAIL_CONFIGURATION_FOUND.equals(smtpHostname) && canConnectToHost();
    }

    private boolean canConnectToHost() {
        try {
            return InetAddress.getByName(smtpHostname).isReachable(5000);
        } catch (ConnectException e) {
            return false;
        } catch (IOException e) {
            return false;
        }
    }

    public static void main(String[] args) {
        Properties smtpProperties = SMTPClient.loadSMTPProperties();
        SMTPClient cl = new SMTPClient();
        cl.sendEmail(smtpProperties.getProperty("smtp.recipient.email"), smtpProperties.getProperty("smtp.sender.email"), "subject", "");
        cl.sendEmail(smtpProperties.getProperty("smtp.recipient.email"), smtpProperties.getProperty("smtp.sender.email"), "subject", null);
        cl.sendEmail(smtpProperties.getProperty("smtp.recipient.email"), smtpProperties.getProperty("smtp.sender.email"), "subject", "text");
    }
}