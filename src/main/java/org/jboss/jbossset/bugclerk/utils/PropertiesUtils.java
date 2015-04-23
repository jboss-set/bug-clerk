package org.jboss.jbossset.bugclerk.utils;

import java.io.IOException;
import java.util.Properties;

import org.jboss.pull.shared.Util;

public final class PropertiesUtils {

    private PropertiesUtils() {

    }

    public static Properties loadPropertiesFile(String filename) {
        try {
            return Util.loadProperties(filename, filename);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

}