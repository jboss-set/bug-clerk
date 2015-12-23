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
package org.jboss.jbossset.bugclerk.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.jbossset.bugclerk.Violation;

// FIME: All methods here should be replaced/enhanced by a closures
public final class CollectionUtils {

    private CollectionUtils() {
    }

    public static Map<String, Violation> indexViolationByCheckname(Collection<Violation> violations) {
        Map<String, Violation> violationIndexedByCheckname = new HashMap<String, Violation>(violations.size());
        for (Violation violation : violations)
            violationIndexedByCheckname.put(violation.getCheckName(), violation);
        return violationIndexedByCheckname;
    }

    public static Map<String, List<Violation>> indexedViolationsByBugId(Collection<Violation> violations) {
        Map<String, List<Violation>> violationIndexedByBugId = new HashMap<String, List<Violation>>(violations.size());
        for (Violation violation : violations) {
            String id = violation.getCandidate().getBug().getTrackerId().get();
            if (!violationIndexedByBugId.containsKey(id)) {
                List<Violation> violationsForBug = new ArrayList<Violation>();
                violationsForBug.add(violation);
                violationIndexedByBugId.put(id, violationsForBug);
            } else
                violationIndexedByBugId.get(id).add(violation);
        }
        return violationIndexedByBugId;
    }
}
