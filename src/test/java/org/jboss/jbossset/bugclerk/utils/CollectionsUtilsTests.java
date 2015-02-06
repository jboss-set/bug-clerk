package org.jboss.jbossset.bugclerk.utils;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.jboss.jbossset.bugclerk.Candidate;
import org.jboss.jbossset.bugclerk.MockUtils;
import org.jboss.jbossset.bugclerk.Violation;
import org.jboss.pull.shared.connectors.bugzilla.Bug;
import org.jboss.pull.shared.connectors.bugzilla.Comment;
import org.junit.Test;

public class CollectionsUtilsTests {

    @Test
    public void testGetEntryOrEmptySet() {
        Map<String, SortedSet<Integer>> map = new HashMap<String, SortedSet<Integer>>();
        SortedSet<Integer> set = new TreeSet<Integer>();
        final String key = "key";
        map.put(key, set);
        SortedSet<Integer> result = CollectionUtils.getEntryOrEmptySet(key, map);
        assertNotNull(result);
        assertEquals(set, result);
        SortedSet<Integer> emptySet = CollectionUtils.getEntryOrEmptySet("not-in", map);
        assertNotNull(emptySet);
        assertTrue(emptySet.isEmpty());
    }

    @Test
    public void testObjectsToArray() {
        Object[] array = new Object[2];
        Object one = new Object();
        Object two = new Object();
        array[0] = one;
        array[1] = two;

        Object[] result = CollectionUtils.objectsToArray(one, two);

        assertNotNull(result);
        assertArrayEquals(array, result);
    }

    @Test
    public void testCreateCandidatList() {
        Map<String, Bug> bugs = new HashMap<String, Bug>(1);
        Bug bug = MockUtils.mockBug(1, "summary");
        String bugId = String.valueOf(bug.getId());

        bugs.put(bugId, bug);

        Map<String, SortedSet<Comment>> comments = new HashMap<String, SortedSet<Comment>>(1);
        Comment comment = MockUtils.mockComment(1, "text", 1);
        SortedSet<Comment> set = new TreeSet<Comment>();
        set.add(comment);
        comments.put(bugId, set);

        List<Candidate> candidates = CollectionUtils.createCandidateList(bugs, comments);
        assertNotNull(candidates);
        assertFalse(candidates.isEmpty());
        assertSame(bug, candidates.get(0).getBug());
        assertSame(set, candidates.get(0).getComments());
    }

    @Test
    public void testIndexedViolationsByBugId() {
        final int bugId = 1;
        List<Violation> violations = MockUtils.mockViolationsListWithOneItem(bugId, "checkname");

        Map<Integer, List<Violation>> result = CollectionUtils.indexedViolationsByBugId(violations);
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(violations, result.get(bugId));
    }

    @Test
    public void testBugSetToIdStringSet() {
        final int value = 1;
        Set<Integer> intSet = new HashSet<Integer>(1);
        intSet.add(value);
        Set<String> result = CollectionUtils.bugSetToIdStringSet(intSet);
        assertNotNull(result);
        assertFalse(result.isEmpty());
        for (String row : result)
            assertEquals(String.valueOf(value), row);
    }

    @Test
    public void testIndexViolationByCheckname() {
        final int bugId = 1;
        final String checkname = "checkname";

        List<Violation> violations = MockUtils.mockViolationsListWithOneItem(bugId, checkname);

        Map<String, Violation> result = CollectionUtils.indexViolationByCheckname(violations);
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(violations.get(0), result.get(checkname));

    }

}
