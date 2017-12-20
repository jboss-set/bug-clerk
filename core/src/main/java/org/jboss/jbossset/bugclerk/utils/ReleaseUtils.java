/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2017, Red Hat, Inc., and individual contributors
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

import java.net.URL;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jboss.set.aphrodite.domain.Release;
import org.jboss.set.aphrodite.domain.StreamComponent;

public class ReleaseUtils {
    private static final Pattern PATTERN_Z = Pattern.compile(".+z\\.");
    private static final Pattern PATTERN_MICRO = Pattern.compile("\\d\\.0\\.");
    private static final Pattern PATTERN_MINOR = Pattern.compile("\\d\\.[1-9]\\.0");
    private static final String NO_MATCH = "";

    private ReleaseUtils() {
    }

    public static String streamReleaseName(final String release) {
        // 7.2.0.GA --> 7.z.0
        // 7.1.z.GA --> 7.1.0
        // 7.0.9.GA --> 7.0.z
        Matcher m = PATTERN_Z.matcher(release);
        if (m.find()) {
            return release.substring(m.start(), m.end() - 1);
        }

        m = PATTERN_MINOR.matcher(release);
        if (m.find()) {
            return release.substring(m.start(), m.end() - 1) + "z";
        }

        m = PATTERN_MICRO.matcher(release);
        if (m.find()) {
            return release.substring(m.start(), m.end()) + "z";
        }
        return null;
    }

    public static String streamReleaseName(final String prefix, final List<Release> releases) {
        return _streamReleaseName(prefix, releases);
    }

    public static String _streamReleaseName(String prefix, List<Release> releases) {
        // hack
        TreeMap<String, Void> lastOrder = new TreeMap<>(new Comparator<String>() {

            public int compare(String obj1, String obj2) {
                if (obj1 == null) {
                    return -1;
                }
                if (obj2 == null) {
                    return 1;
                }
                if (obj1.equals(obj2)) {
                    return 0;
                }
                return obj1.compareTo(obj2);
            }
        });
        for (Release release : releases) {
            lastOrder.put(release.getVersion().get(), null);
        }
        if (lastOrder.size() == 0)
            return NO_MATCH;
        return prefix + streamReleaseName(lastOrder.firstEntry().getKey());
    }

    public static StreamComponent getStreamComponent(final Collection<StreamComponent> streamComponents, final URL url) {
        final String urlToMatch = sanitazeURL(url.toString());
        for (StreamComponent streamComponent : streamComponents) {
            if (sanitazeURL(streamComponent.getRepositoryURL().toString()).equals(urlToMatch))
                return streamComponent;
        }
        return null;
    }

    private static String sanitazeURL(String string) {
        if (string.endsWith(".git")) {
            string = string.substring(0, string.lastIndexOf(".git"));
        }
        return string;
    }

}
