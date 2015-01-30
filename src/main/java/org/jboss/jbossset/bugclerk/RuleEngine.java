package org.jboss.jbossset.bugclerk;

import java.util.Collection;

import org.drools.core.ClassObjectFilter;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.AgendaFilter;
import org.kie.api.runtime.rule.Match;

public class RuleEngine {

    private final KieSession ksession;

    public RuleEngine(String sessionName) {
        ksession = createKSession(sessionName);
    }

    public static KieSession createKSession(final String sessionName) {
        try {
            return KieServices.Factory.get().getKieClasspathContainer().newKieSession(sessionName);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public Collection<Violation> processBugEntry(Collection<Candidate> candidates) {
        addCandidatesToFacts(candidates);
        ksession.fireAllRules();
        return retrieveViolationsFromKSession(ksession);

    }

    @SuppressWarnings("unchecked")
    private static Collection<Violation> retrieveViolationsFromKSession(final KieSession ksession) {
        return (Collection<Violation>) ksession.getObjects(new ClassObjectFilter(Violation.class));
    }

    private void addCandidatesToFacts(Collection<Candidate> candidates) {
        for (Candidate fact : candidates)
            ksession.insert(fact);

    }

    private AgendaFilter createAgendaForCheck(final String checkname) {
        return new AgendaFilter() {
            @Override
            public boolean accept(Match match) {
                return match.getRule().getName().equals(checkname);
            }
        };

    }

    public Collection<Violation> runCheckOnBugs(final String checkname, Collection<Candidate> candidates) {
        addCandidatesToFacts(candidates);
        ksession.fireAllRules(createAgendaForCheck(checkname));
        return retrieveViolationsFromKSession(ksession);

    }

    @Deprecated
    public Collection<Violation> processBugEntry(Object[] facts) {
        throw new UnsupportedOperationException("This method is deprecated - use the typed method.");
    }

    public void shutdownRuleEngine() {
        if (ksession != null)
            ksession.dispose();
        else
            throw new IllegalStateException("Instance of " + this.getClass()
                    + " was not properly initiated, or is dirty: KSession pointer is 'null'.");
    }
}