package org.jboss.jbossset.bugclerk.aphrodite;

public class AphroditeParameters {

    final String trackerUrl;
    final String username;
    final String password;

    public AphroditeParameters(String trackerUrl, String username, String password) {
        this.trackerUrl = trackerUrl;
        this.username = username;
        this.password = password;
    }

    public String getTrackerUrl() {
        return trackerUrl;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((password == null) ? 0 : password.hashCode());
        result = prime * result + ((trackerUrl == null) ? 0 : trackerUrl.hashCode());
        result = prime * result + ((username == null) ? 0 : username.hashCode());
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
        AphroditeParameters other = (AphroditeParameters) obj;
        if (password == null) {
            if (other.password != null)
                return false;
        } else if (!password.equals(other.password))
            return false;
        if (trackerUrl == null) {
            if (other.trackerUrl != null)
                return false;
        } else if (!trackerUrl.equals(other.trackerUrl))
            return false;
        if (username == null) {
            if (other.username != null)
                return false;
        } else if (!username.equals(other.username))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "AphroditeParameters [trackerUrl=" + trackerUrl + ", username=" + username + ", password=" + password + "]";
    }
}
