package org.jboss.jbossset.bugclerk;

import org.jboss.pull.shared.connectors.bugzilla.Bug;

public class Candidate {

    private final Bug bug;
    private boolean isCandidate = true;
    private boolean filtered = false;

    public Candidate(Bug bug) {
        this.bug = bug;
    }

    public boolean isCandidate() {
        return isCandidate;
    }

    public void setCandidate(boolean isCandidate) {
        this.isCandidate = isCandidate;
    }

    public boolean isFiltered() {
        return filtered;
    }

    public void setFiltered(boolean filtered) {
        this.filtered = filtered;
    }

    public Bug getBug() {
        return bug;
    }

    @Override
    public String toString() {
        return "Candidate [bug=" + bug.getId() + ", isCandidate=" + isCandidate + "]";
    }
}
