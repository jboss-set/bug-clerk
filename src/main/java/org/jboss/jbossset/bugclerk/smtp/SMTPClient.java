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
package org.jboss.jbossset.bugclerk.smtp;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.jboss.jbossset.bugclerk.BugClerk;
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

    static Properties loadSMTPProperties() {
        try {
            return Util.loadProperties(BugClerk.CONFIGURATION_FILENAME, BugClerk.CONFIGURATION_FILENAME);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private void createAndSendEMail(String to, String from, String subject, String text) throws MessagingException {
        MimeMessage message = new MimeMessage(getSession());
        message.setFrom(new InternetAddress(from));
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
        message.setSubject(subject);
        message.setText(text);
        Transport.send(message);
    }

    public void sendEmail(String to, String from, String subject, String text) {
        try {
            if (emailConfigured() && !isEmailEmpty(text))
                createAndSendEMail(to, from, subject, text);
            else
                System.err.println("No configuration for email found or empty mail - skipping email.");
        } catch (MessagingException mex) {
            throw new IllegalStateException(mex);
        }
    }

    private boolean isEmailEmpty(String text) {
        return (text == null || "".equals(text));
    }

    private boolean emailConfigured() {
        return !NO_EMAIL_CONFIGURATION_FOUND.equals(smtpHostname) && canConnectToHost();
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

}