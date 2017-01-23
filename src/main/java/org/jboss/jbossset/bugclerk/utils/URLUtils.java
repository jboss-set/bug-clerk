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
package org.jboss.jbossset.bugclerk.utils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class URLUtils {

    private static final String SLASH = "/";

    private static final String BZ_FILTERNAME_URL_PARAMNAME = "namedcmd=";

    private URLUtils() {
    }

    public static URL createURLFromString(String URL) {
        try {
            return new URL(URL);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid URL:" + URL, e);
        }
    }

    public static String buildBzUrlPrefix(URL bzURL) {
        return throwExceptionIfURLisNull(bzURL).getProtocol() + "://" + bzURL.getHost() + "/show_bug.cgi?id=";
    }

    private static URL throwExceptionIfURLisNull(URL url) {
        if (url == null)
            throw new IllegalArgumentException("Can't invoke with a 'null' URL.");
        return url;
    }

    public static String extractParameterValueIfAny(final URL url, final String param) {
        if (param == null || "".equals(param))
            throw new IllegalArgumentException("Can't invoke with a 'null' or empty param.");

        final String query = url.getQuery();
        if (query == null || "".equals(query))
            throw new IllegalArgumentException("Can't invok with a 'null' or empty query.");

        final int indexOfParam = query.indexOf(param);
        if (indexOfParam == -1)
            return "";
        final String subString = query.substring(indexOfParam);
        int indexEndOfParam = subString.indexOf("&");
        if (indexEndOfParam == -1)
            indexEndOfParam = subString.length();
        return subString.substring(param.length(), indexEndOfParam);
    }


    public static String getServerUrl(URL tracker) {
        return tracker.getProtocol() + ":" + SLASH + SLASH + tracker.getHost() + SLASH;
    }

    public static String getServerUrl(String url) {
        return getServerUrl(createURLFromString(url));
    }

    public static String extractFilterNameOrReturnFilterURL(String url) throws MalformedURLException {
        String filterName = URLUtils.extractParameterValueIfAny(new URL(url), BZ_FILTERNAME_URL_PARAMNAME);
        return ("".equals(filterName) ? url.toString() : filterName);
    }

    /*
     * Copy and paste from Stackoverlow:
     * http://stackoverflow.com/questions/1806017/extracting-urls-from-a-text-document-using-java-regular-expressions Thanks to
     * Philip Daubmeier.
     */
    public static List<String> extractUrls(String input) {

        Pattern pattern = Pattern.compile("\\b(((ht|f)tp(s?)\\:\\/\\/|~\\/|\\/)|www.)"
                + "(\\w+:\\w+@)?(([-\\w]+\\.)+(com|org|net|gov" + "|mil|biz|info|mobi|name|aero|jobs|museum"
                + "|travel|[a-z]{2}))(:[\\d]{1,5})?" + "(((\\/([-\\w~!$+|.,=]|%[a-f\\d]{2})+)+|\\/)+|\\?|#)?"
                + "((\\?([-\\w~!$+|.,*:]|%[a-f\\d{2}])+=?" + "([-\\w~!$+|.,*:=]|%[a-f\\d]{2})*)"
                + "(&(?:[-\\w~!$+|.,*:]|%[a-f\\d{2}])+=?" + "([-\\w~!$+|.,*:=]|%[a-f\\d]{2})*)*)*"
                + "(#([-\\w~!$+|.,*:=]|%[a-f\\d]{2})*)?\\b");

        Matcher matcher = pattern.matcher(input);
        List<String> result = new ArrayList<String>(matcher.groupCount());
        while (matcher.find()) {
            result.add(matcher.group());
        }
        return result;
    }

    public static String extractJiraTrackerId(URL bugId) {
        if ( bugId != null && bugId.getQuery().contains("/") )
            return bugId.getQuery().substring(bugId.getQuery().lastIndexOf("/"));
        return "";
    }

}
