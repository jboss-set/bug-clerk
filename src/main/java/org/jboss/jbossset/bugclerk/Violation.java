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
