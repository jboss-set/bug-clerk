package org.jboss.jbossset.bugclerk;

import com.beust.jcommander.Parameter;

public class Arguments {

	// TODO make a converter to check ID
	@Parameter(names = { "-i", "--url-issue-id" }, description = "Issue ID", required = true)
	private String issueId;

	@Parameter(names = { "-h", "--help" }, description = "print help text", required = false)
	private boolean help = false;

    @Parameter(names = { "-d", "--debug" }, description = "debug mode", required = false)
    private boolean debug = false;

	public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public boolean isHelp() {
		return help;
	}

	public void setHelp(boolean help) {
		this.help = help;
	}

	public String getIssueId() {
		return issueId;
	}

	public void setIssueId(String issueId) {
		this.issueId = issueId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (help ? 1231 : 1237);
		result = prime * result + ((issueId == null) ? 0 : issueId.hashCode());
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
		Arguments other = (Arguments) obj;
		if (help != other.help)
			return false;
		if (issueId == null) {
			if (other.issueId != null)
				return false;
		} else if (!issueId.equals(other.issueId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Arguments [issueId=" + issueId + ", help=" + help + "]";
	}

}
