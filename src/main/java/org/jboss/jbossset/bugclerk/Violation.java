package org.jboss.jbossset.bugclerk;

import org.jboss.pull.shared.connectors.bugzilla.Bug;

public class Violation {

    private final Bug bug;
    private final String message;

    private void constructorSanityCheck(Bug bug, String mssg) {
        if ( bug == null )
            throw new IllegalArgumentException("Can't instantiate " + this.getClass().getCanonicalName() + " withou a 'null' bug ref.");
        if ( mssg == null || "".equals(mssg))
            throw new IllegalArgumentException("Can't instantiate " + this.getClass().getCanonicalName() + " withou a 'null' or empty message.");
    }

    public Violation(Bug bug, String message) {
        constructorSanityCheck(bug, message);
        this.bug = bug;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public Bug getBug() {
        return bug;
    }

    @Override
    public String toString() {
        return "Violation [bugId=" + bug.getId() + ", message=" + message + "]";
    }
}
