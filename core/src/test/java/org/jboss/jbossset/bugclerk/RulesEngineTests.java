package org.jboss.jbossset.bugclerk;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.jboss.set.aphrodite.domain.Issue;
import org.junit.Test;

public class RulesEngineTests extends AbstractCheckRunner {

    @Test
    public void loadExternalRules() throws IOException {      
      Issue mock = MockUtils.mockBug("JBEAP-669", "External Rule Violation");
      InputStream stream = RulesEngineTests.class.getResourceAsStream("/org/jboss/jbossset/bugclerk/ExternalRule.drl");
      assert stream != null;
      assert engine.runCheckOnBugs(Arrays.asList(new Candidate(mock)), "ExternalRule").size() == 1;
    }
}
