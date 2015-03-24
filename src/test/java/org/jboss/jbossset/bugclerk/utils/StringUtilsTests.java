package org.jboss.jbossset.bugclerk.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class StringUtilsTests {

    @Test
    public void testFormatCheckname() {
        final String checkname = "check";
        String expected = StringUtils.OPEN_ID_SEPARATOR + checkname + StringUtils.CLOSE_ID_SEPARATOR;
        String result = StringUtils.formatCheckname(checkname);

        assertEquals(expected, result);
    }

    @Test
    public void testTwoEOLs() {
        String expected = StringUtils.EOL + StringUtils.EOL;
        StringBuffer result = StringUtils.twoEOLs();

        assertEquals(expected, result.toString());
    }

    @Test
    public void testEmptyOrNull() {
        assertEquals(StringUtils.emptyOrNull(null), true);
        assertEquals(StringUtils.emptyOrNull(""), true);
        assertEquals(StringUtils.emptyOrNull("content"), false);
    }
}
