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
package org.jboss.jbossset.bugclerk;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.jboss.pull.shared.connectors.bugzilla.Bug;
import org.jboss.pull.shared.connectors.bugzilla.Comment;
import org.jboss.pull.shared.connectors.common.Flag;

public class Candidate {

    private final Bug bug;
    private final SortedSet<Comment> comments;
    @SuppressWarnings("rawtypes")
    private final Map<MetadataType, List> metadata;

    private boolean isCandidate = true;
    private boolean filtered = false;

    private static void checkIfNotNull(Object ref, String fieldName) {
        if (ref == null) {
            throw new IllegalArgumentException("Can't build instance of " + Candidate.class.getCanonicalName()
                    + " with 'null' value for field:" + fieldName);
        }
    }

    public Candidate(Bug bug, SortedSet<Comment> comments) {
        checkIfNotNull(bug, "bug");
        checkIfNotNull(comments, "comments");
        this.bug = bug;
        this.comments = comments;
        this.metadata = initMetadatas();
    }

    public Candidate(Bug bug) {
        checkIfNotNull(bug, "bug");
        this.bug = bug;
        this.comments = new TreeSet<Comment>();
        this.metadata = initMetadatas();
    }

    @SuppressWarnings("rawtypes")
    private Map<MetadataType, List> initMetadatas() {
        Map<MetadataType, List> metadatas = new HashMap<MetadataType, List>(0);
        metadatas.put(MetadataType.PULL_REQUESTS, new ArrayList<URL>(0));
        metadatas.put(MetadataType.BUGCLERK_IGNORES, new ArrayList<String>(0));
        return metadatas;
    }

    // FIXME: Following methods are workaround,
    // find a nicer way to implements using drools/MVEL
    public String getFlagNamesContaining(String pattern) {
        if (pattern == null || "".equals(pattern))
            throw new IllegalArgumentException("Can't invoke with an empty or 'null' pattern.");

        StringBuffer res = null;
        for (Flag flag : bug.getFlags()) {
            if (flag.getName().contains(pattern)) {
                if (res == null)
                    res = new StringBuffer();
                else
                    res.append(",");
                res.append(flag.getName());
            }
        }

        return (res != null ? res.toString() : "");
    }

    @SuppressWarnings("unchecked")
    public void addRuleToIgnore(String rulePattern) {
        this.metadata.get(MetadataType.BUGCLERK_IGNORES).add(rulePattern.substring(rulePattern.indexOf("#") +1 ));
    }

    @SuppressWarnings("unchecked")
    public void addPR(String pullRequest) {
        try {
            this.metadata.get(MetadataType.PULL_REQUESTS).add(new URL(pullRequest));
        } catch (MalformedURLException e) {
            System.out.println("Malformed Pull Request URL:" + pullRequest);
        }
    }

    // End of workarounds

    public boolean isCandidate() {
        return isCandidate;
    }

    public void setCandidate(boolean isCandidate) {
        this.isCandidate = isCandidate;
    }

    public boolean isFiltered() {
        return filtered;
    }

    public void setFiltered(boolean filtered) {
        this.filtered = filtered;
    }

    public Bug getBug() {
        return bug;
    }

    public SortedSet<Comment> getComments() {
        return comments;
    }

    @SuppressWarnings("rawtypes")
    public Map<MetadataType, List> getMetadata() {
        return metadata;
    }

    @Override
    public String toString() {
        return "Candidate [bug=" + bug + ", comments=" + comments + ", metadata=" + metadata + ", isCandidate=" + isCandidate
                + ", filtered=" + filtered + "]";
    }
}
