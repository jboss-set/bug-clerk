package org.jboss.jbossset.bugclerk.cli;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jboss.pull.shared.connectors.bugzilla.Bug;
import org.jboss.pull.shared.connectors.bugzilla.Bug.Status;
import org.jboss.pull.shared.connectors.bugzilla.BugsClient;
import org.jboss.pull.shared.connectors.common.Flag;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

public class AssignBZCLI {

    private static final int INVALID_COMMAND_INPUT = 1;

    private static final String BUGZILLA_URL = "https://bugzilla.redhat.com/";

    public static void main(String[] args) {
        AssignBZArguments arguments = extractParameters(new AssignBZArguments(), args);
        BugsClient client = new BugsClient(BUGZILLA_URL, arguments.getUsername(), arguments.getPassword());
        Bug bug = client.getBug(Integer.valueOf(arguments.getBugId()));
        Map<String, Boolean> map = buildFlagsMap();
        List<Flag> flags = bug.getFlags();

        for (Flag flag : flags)
            if (map.containsKey(flag.getName()))
                map.put(flag.getName(), true);

        for (Entry<String, Boolean> entry : map.entrySet())
            if (!entry.getValue())
                flags.add(new Flag(entry.getKey(), arguments.getAssignedTo(), Flag.Status.UNKNOWN));

        Map<String, Object> params = new HashMap<String, Object>(4);
        params.put("ids", arguments.getBugId());
        params.put("status", Status.ASSIGNED.toString());
        params.put("estimated_time", arguments.getEstimate());
        params.put("assigned_to", arguments.getAssignedTo());
        params.put("flags", buildFlagsAsStringArray(flags));
        client.customOperation("Bug.update", params);
    }

    private static Object[] buildFlagsAsStringArray(List<Flag> flags) {
        Object[] objects = new Object[flags.size()];
        int i = 0;
        for ( Flag flag : flags ) {
            objects[i] = flag.getSetter() + " set " + flag.getName() + " to UNKNOWN";
            System.out.println(objects[i]);
            i++;
        }
        return objects;
    }

    private static Object[] buildFlagsAsMapArray(List<Flag> flags) {
        Object[] objects = new Object[flags.size()];
        int i = 0;
        for (Flag flag : flags) {
            Map<String, Object> flagAsMap = new HashMap<String, Object>(3);
            flagAsMap.put("name", flag.getName());
            flagAsMap.put("setter", flag.getSetter());
            flagAsMap.put("status", turnIntoSymbol(flag.getStatus()));
            objects[i++] = flagAsMap;
        }
        return objects;
    }

    private static String turnIntoSymbol(Flag.Status status) {

        switch (status) {
            case UNKNOWN:
                return "?";
            case UNSET:
                return " ";
            case POSITIVE:
                return "+";
            case NEGATIVE:
                return "-";
        }
        throw new IllegalStateException("Invalid Status:" + status);
    }

    private static Map<String, Boolean> buildFlagsMap() {
        Map<String, Boolean> map = new HashMap<String, Boolean>(3);
        map.put("pm_ack", false);
        map.put("devel_ack", false);
        map.put("qa_ack", false);
        return map;
    }

    protected static AssignBZArguments extractParameters(AssignBZArguments arguments, String[] args) {
        JCommander jcommander = null;
        try {
            jcommander = new JCommander(arguments, args);
            jcommander.setProgramName(AssignBZCLI.class.getSimpleName().toLowerCase());
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

}
