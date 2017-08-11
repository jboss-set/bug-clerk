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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jboss.set.aphrodite.domain.Issue;

public class Candidate {

    private final Issue bug;
    private final Set<String> checksToBeIgnored = new HashSet<String>(0);
    private final List<Violation> violations = new ArrayList<Violation>(0);

    private boolean isCandidate = true;
    private boolean filtered = false;

    private static void checkIfNotNull(Object ref, String fieldName) {
        if (ref == null) {
            throw new IllegalArgumentException("Can't build instance of " + Candidate.class.getCanonicalName()
                    + " with 'null' value for field:" + fieldName);
        }
    }

    public Candidate(Issue bug) {
        checkIfNotNull(bug, "bug");
        this.bug = bug;
    }

    public void addRuleToIgnore(String rulePattern) {
        this.checksToBeIgnored.add(rulePattern.substring(rulePattern.indexOf("#") + 1));
    }

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

    public Issue getBug() {
        return bug;
    }

    public Set<String> getChecksToBeIgnored() {
        return checksToBeIgnored;
    }

    public void addViolation(Violation violation) {
        this.violations.add(violation);
    }

    /*
     * Some violation maybe be reported several times by drools engine,
     * it used to be compensante by using insertLogical (instead of insert)
     * but now Violation instead are directly added to the candidate's
     * violation list, so the folllowing methods is a work around that.
     *
     * Note: using is not option, as some rules may needs to report several
     * violations on one Issue instance.
     */
    public void addViolationOnce(Violation violation) {
        if ( ! this.violations.isEmpty() )
            for ( Violation v : this.violations )
                if ( v.getCheckName().equals(violation.getCheckName()) )
                        return;
        this.violations.add(violation);
    }

    public List<Violation> getViolations() {
        return violations;
    }

    @Override
    public String toString() {
        return "Candidate [bug=" + bug + ", checksToBeIgnored=" + checksToBeIgnored + ", violations=" + violations
                + ", isCandidate=" + isCandidate + ", filtered=" + filtered + "]";
    }
}
