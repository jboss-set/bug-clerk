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

import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.drools.core.ClassObjectFilter;
import org.jboss.jbossset.bugclerk.aphrodite.AphroditeClient;
import org.jboss.set.aphrodite.domain.Issue;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.AgendaFilter;
import org.kie.api.runtime.rule.Match;

public class RulesEngine {

    private final KieSession ksession;
    static final String KIE_SESSION = "BzCheck";

    public RuleEngine(Map<String, Object> externalGlobals,AphroditeClient client) {
        Map<String, Object> globals = buildGlobalsMap(client);
        if (! externalGlobals.isEmpty() )
            globals.putAll(externalGlobals);
        ksession = createKSession(KIE_SESSION);
        globals.entrySet().forEach(e -> ksession.getGlobals().set(e.getKey(), e.getValue()));
    }

    protected Map<String, Object> buildGlobalsMap(AphroditeClient client) {
        Map<String, Object> globalsMaps = new HashMap<String, Object>(2);
        globalsMaps.put("issuesIndexedByURL", new HashMap<URL,Issue>());
        globalsMaps.put("payloadTrackerIndexedByURL", new HashMap<URL,Issue>());
        if ( client != null ) globalsMaps.put("aphrodite", client);
        return globalsMaps;
    }

    public static KieSession createKSession(final String sessionName) {
        try {
            return KieServices.Factory.get().getKieClasspathContainer().newKieSession(sessionName);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public Collection<Candidate> processBugEntry(Collection<Candidate> candidates) {
        addCandidatesToFacts(candidates);
        ksession.fireAllRules();
        return retrieveViolationsFromKSession(ksession);
    }

    @SuppressWarnings("unchecked")
    private static Collection<Candidate> retrieveViolationsFromKSession(final KieSession ksession) {
        return (Collection<Candidate>) ksession.getObjects(new ClassObjectFilter(Candidate.class));
    }

    private void addCandidatesToFacts(Collection<Candidate> candidates) {
        candidates.forEach(c -> ksession.insert(c));
    }

    private AgendaFilter createAgendaForCheck(final Collection<String> checknames) {
        return new AgendaFilter() {
            @Override
            public boolean accept(Match match) {
                return checknames.contains(match.getRule().getName());
            }
        };
    }

    public Collection<Candidate> runChecksOnBugs(Collection<Candidate> candidates, final Collection<String> checknames) {
        addCandidatesToFacts(candidates);
        ksession.fireAllRules(createAgendaForCheck(checknames));
        return retrieveViolationsFromKSession(ksession);
    }

    public Collection<Candidate> runCheckOnBugs(Collection<Candidate> candidates, final String checkname) {
        return runChecksOnBugs(candidates, Arrays.asList(checkname));
    }

    @SuppressWarnings("unchecked")
    public Collection<Candidate> filterBugs(final String checkname, Collection<Candidate> candidates) {
        addCandidatesToFacts(candidates);
        ksession.fireAllRules(createAgendaForCheck(Arrays.asList(checkname)));
        return (Collection<Candidate>) ksession.getObjects(new ClassObjectFilter(Candidate.class));
    }

    public void shutdownRuleEngine() {
        if (ksession == null)
            throw new IllegalStateException("Instance of " + this.getClass()
                    + " was not properly initiated, or is dirty: KSession pointer is 'null'.");
        else {
            ksession.destroy();
        }
    }
}
