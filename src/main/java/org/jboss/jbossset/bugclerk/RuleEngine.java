package org.jboss.jbossset.bugclerk;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.drools.core.ClassObjectFilter;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

public class RuleEngine {

	private final KieSession ksession;

	private final List<Object> contextItems = new ArrayList<Object>();

	public RuleEngine(String sessionName) {
		try {
	        KieServices ks = KieServices.Factory.get();
	        KieContainer kc = ks.getKieClasspathContainer();
        	// its definition and configuration in the META-INF/kmodule.xml file
	        ksession = kc.newKieSession(sessionName);
	        contextItems.add("ContextItemString");
	        ksession.setGlobal("list", contextItems );

	        // To setup a file based audit logger, uncomment the next line
	        // KieRuntimeLogger logger = ks.getLoggers().newFileLogger( ksession, "./helloworld" );

	        // To setup a ThreadedFileLogger, so that the audit view reflects events whilst debugging,
	        // uncomment the next line
	        // KieRuntimeLogger logger = ks.getLoggers().newThreadedFileLogger( ksession, "./helloworld", 1000 );

		} catch ( Exception e) {
			throw new IllegalStateException(e);
		}
	}

	@SuppressWarnings("unchecked")
    public Collection<Violation> processBugEntry(Object[] facts) {
	    for ( Object fact : facts ) {
	        if ( fact instanceof Collection ) {
	            @SuppressWarnings("rawtypes")
                Collection collection = (Collection)fact;
	            for ( Object o : collection )
	                ksession.insert( o );
	        } else
	            ksession.insert( fact );
	    }
	     // and fire the rules
        ksession.fireAllRules();
        return (Collection<Violation>) ksession.getObjects(new ClassObjectFilter(Violation.class));
	}

	public void shutdownRuleEngine() {
		if ( ksession != null )
			ksession.dispose();
		else
			throw new IllegalStateException("Instance of " + this.getClass() + " was not properly initiated, or is dirty: KSession pointer is 'null'.");

        // Remove comment if using logging
        // logger.close();

	}
}
