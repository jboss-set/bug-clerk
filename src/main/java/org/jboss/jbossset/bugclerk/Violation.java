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

import org.jboss.pull.shared.connectors.bugzilla.Bug;

public class Violation {

    private final Bug bug;
    private final String message;
    private final String checkName;

    private void constructorSanityCheck(Bug bug, String mssg) {
        if (bug == null)
            throw new IllegalArgumentException("Can't instantiate " + this.getClass().getCanonicalName()
                    + " withou a 'null' bug ref.");
        if (mssg == null || "".equals(mssg))
            throw new IllegalArgumentException("Can't instantiate " + this.getClass().getCanonicalName()
                    + " withou a 'null' or empty message.");
    }

    public Violation(Bug bug, String checkName, String message) {
        constructorSanityCheck(bug, message);
        this.bug = bug;
        this.message = message;
        this.checkName = checkName;
    }

    public String getMessage() {
        return message;
    }

    public Bug getBug() {
        return bug;
    }

    public String getCheckName() {
        return checkName;
    }

    @Override
    public String toString() {
        return "Violation [bugId=" + bug.getId() + ", check=" + checkName + ", message=" + message + "]";
    }
}
