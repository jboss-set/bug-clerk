package org.jboss.jbossset.bugclerk;

import java.util.ArrayList;
import java.util.List;

import com.beust.jcommander.IVariableArity;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;

public class Arguments implements IVariableArity {

	// TODO make a converter to turn directly into an URL
	@Parameter(names = { "-u", "--url-prefix" }, description = "URL prefix (before the issue ID)", required = true)
	private String urlPrefix;

	@Parameter(description = "Issue IDs", variableArity = true)
	private final List<String> ids = new ArrayList<String>();

	@Parameter(names = { "-h", "--help" }, description = "print help text", required = false)
	private boolean help = false;

    @Parameter(names = { "-d", "--debug" }, description = "debug mode", required = false)
    private boolean debug = false;

    private static final String PROG_NAME = BugClerk.class.getSimpleName().toLowerCase();

    private static final int INVALID_COMMAND_INPUT = 1;

    public static Arguments extractParameters(String[] args) {
        Arguments arguments = new Arguments();
        JCommander jcommander = null;
        try {
            jcommander = new JCommander(arguments, args);
            jcommander.setProgramName(PROG_NAME);
            if (arguments.isHelp()) {
                jcommander.usage();
                System.exit(0);
            }

        } catch (ParameterException e) {
            System.out.println(e.getMessage());
            System.exit(INVALID_COMMAND_INPUT);
        }
        return arguments;
    }

    public static Arguments validateArgs(Arguments arguments) {
        return arguments;
    }


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

	public String getUrlPrefix() {
        return urlPrefix;
    }

    public void setUrlPrefix(String urlPrefix) {
        this.urlPrefix = urlPrefix;
    }

    public List<String> getIds() {
        return ids;
    }

    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (help ? 1231 : 1237);
		result = prime * result + ((urlPrefix == null) ? 0 : urlPrefix.hashCode());
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
		if (urlPrefix == null) {
			if (other.urlPrefix != null)
				return false;
		} else if (!urlPrefix.equals(other.urlPrefix))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Arguments [issueId=" + urlPrefix + ", help=" + help + "]";
	}

    @Override
    public int processVariableArity(String optionName, String[] options) {
        if ( "-i".equals(optionName) || "--ids".equals(optionName) ) {
            for ( String id : options ) {
                this.ids.add(id);
            }
            return this.ids.size();
        }
        return 0;
    }

}
