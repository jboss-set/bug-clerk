package org.jboss.jbossset.bugclerk.utils;

public final class StringUtils {

    private StringUtils() {}

    public static final String EOL = "\n";
    public static final String ITEM_ID_SEPARATOR = ") ";


    public static StringBuffer twoEOLs() {
        return new StringBuffer(EOL).append(EOL);
    }

}

