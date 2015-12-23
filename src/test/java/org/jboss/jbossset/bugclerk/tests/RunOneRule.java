package org.jboss.jbossset.bugclerk.tests;

import java.io.File;

import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message.Level;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.internal.io.ResourceFactory;

public class RunOneRule {

    @Test
    public void test() {
        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem();
        kfs.write(ResourceFactory.newFileResource(new File(System.getProperty("user.dir")
                + "/src/test/resources/AssignedButStillOnSET.drl")));
        KieBuilder kbuilder = ks.newKieBuilder(kfs);
        // Build the KieBases
        kbuilder.buildAll();
        // Check for errors
        if (kbuilder.getResults().hasMessages(Level.ERROR)) {
            throw new IllegalArgumentException(kbuilder.getResults().toString());
        }
        ReleaseId relId = kbuilder.getKieModule().getReleaseId();
        KieContainer kcontainer = ks.newKieContainer(relId);
        KieBaseConfiguration kbconf = ks.newKieBaseConfiguration();
        KieBase kbase = kcontainer.newKieBase(kbconf);
        KieSessionConfiguration ksconf = ks.newKieSessionConfiguration();
        KieSession ksession = kbase.newKieSession(ksconf, null);
        ksession.insert(new Object());
        assert ksession != null;
        ksession.fireAllRules();
    }

}
