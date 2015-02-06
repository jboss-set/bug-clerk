package org.jboss.jbossset.bugclerk.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.net.URL;

import org.junit.Test;

public class URLUtilsTest {

    @Test
    public void testThrowExceptionIfInvalidURL() {
        try {
            URLUtils.createURLFromString("invalidURl");
        } catch (IllegalArgumentException e) {
            return; //pass
        }
        fail("should have thrown an instance of " + IllegalArgumentException.class);
    }

    @Test
    public void testBuildURL() {
        final String host = "github.com";
        final String path = "/jboss-set";
        final String urlAsString = "http://" + host + path;

        URL url = URLUtils.createURLFromString(urlAsString);
        assertNotNull(url);
        assertEquals(url.getHost(), host);
        assertEquals(url.getPath(), path);
    }
}
