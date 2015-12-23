package org.jboss.jbossset.bugclerk.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.List;
import java.util.Map;

import org.jboss.jbossset.bugclerk.MockUtils;
import org.jboss.jbossset.bugclerk.Violation;
import org.junit.Test;

public class CollectionsUtilsTest {

    @Test
    public void testIndexViolationByCheckname() {
        final String bugId = "1";
        final String checkname = "checkname";

        List<Violation> violations = MockUtils.mockViolationsListWithOneItem(bugId, checkname);

        Map<String, Violation> result = CollectionUtils.indexViolationByCheckname(violations);
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(violations.get(0), result.get(checkname));

    }

}
