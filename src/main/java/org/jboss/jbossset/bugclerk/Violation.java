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

public class Violation {

    private final String message;
    private final String checkName;
    private Severity level = Severity.MINOR;

    public Violation(String checkName, String message) {
        this.message = message;
        this.checkName = checkName;
    }

    public Violation(String checkName, String message, Severity level) {
        this(checkName, message);
        this.level = level;
    }

    public String getMessage() {
        return message;
    }

    public String getCheckName() {
        return checkName;
    }

    public Severity getLevel() {
        return level;
    }

    public void setLevel(Severity level) {
        this.level = level;
    }

    @Override
    public String toString() {
        return "Violation [message=" + message + ", checkName=" + checkName + ", level=" + level
                + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((checkName == null) ? 0 : checkName.hashCode());
        result = prime * result + ((level == null) ? 0 : level.hashCode());
        result = prime * result + ((message == null) ? 0 : message.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;

        Violation other = (Violation) obj;

        if (checkName == null) {
            if (other.checkName != null)
                return false;
        } else if (!checkName.equals(other.checkName))
            return false;

        if (message == null) {
            if (other.message != null)
                return false;
        } else if (!message.equals(other.message))
            return false;
        return true;
    }

}
