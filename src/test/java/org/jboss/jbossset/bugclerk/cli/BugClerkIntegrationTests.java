package org.jboss.jbossset.bugclerk.cli;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/*
 * Can only be run if a .bugzillarc file exists and contains username's password.
 */
public class BugClerkIntegrationTests {

    private final static String REDHAT_BZ_SERVER_URL = "https://bugzilla.redhat.com";
    private final static String URL_TO_REDHAT_BZ = REDHAT_BZ_SERVER_URL + "/show_bug.cgi?id=";

    private static String username = "rpelisse@redhat.com";
    private static String password;

    private String reportFilename;

    private static String readPasswordFromBugzillaRCFile(String folder, String filename) throws IOException {
        final Optional<String> hasPassword = xxx(folder, filename);
        if(hasPassword.isPresent())
                return hasPassword.get().split(" = ")[1];
        throw new IllegalStateException("Can't find username's password.");
    }

    private static Optional<String> xxx(String folder, String filename) throws IOException {
            Stream<String> lines = Files.lines(Paths.get(folder,filename));
            Optional<String> passwordLine = lines.filter(s -> s.startsWith("password")).findFirst();
            lines.close();
            return passwordLine;
    }

    @BeforeClass
    public static void loadPassword() throws IOException, URISyntaxException {
        password = readPasswordFromBugzillaRCFile(System.getProperty("user.home"), ".bugzillarc");
    }

    @Before
    public void prepareTest() throws IOException {
        File temp = File.createTempFile("bugclerk-report.html", ".tmp");
        reportFilename = temp.getAbsolutePath();
    }

    @After
    public void deleteTmpFiles() throws IOException {
        new File(reportFilename).delete();
    }

    @Test
    public void runOnPotentialSETIssue() {
        final String filterName = REDHAT_BZ_SERVER_URL
                + "/buglist.cgi?cmdtype=dorem&list_id=3394903&namedcmd=Potential JBoss SET EAP 6 issues&remaction=run&sharer_id=213224&ctype=csv";
        runBugClerkWithFiltername(reportFilename, filterName);
        File file = new File(reportFilename);
        if (!file.exists())
            fail("No report generated.");
    }

    private static void runBugClerk(String[] args) {
        try {
            BugClerkCLI.main(args);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void runOnSeveralBZs() {
        runBugClerk(argsWithIds("1187026", "1187027", "1185118"));
        runBugClerk(argsWithIds("1039989", "1039989", "1185118"));
    }

    @Test
    public void runOnBZ1184440() {
        runBugClerk(argsWithIds("1184440"));
    }

    @Test
    public void runOnClosedBZ() {
        runBugClerk(argsWithIds("1199194"));
    }

    private static String[] addAuthParameters(String[] args) {
        int i = 0;
        args[i++] = "-u";
        args[i++] = username;
        args[i++] = "-p";
        args[i++] = password;
        return args;
    }


    private static String[] argsWithIds(String... ids) {
        int pos = 4;
        String[] args = new String[pos + ids.length];
        args = addAuthParameters(args);
        for ( String id : ids )
            args[pos++] = URL_TO_REDHAT_BZ + id;
        return args;
    }

    private static void runBugClerkWithFiltername(String reportFilename, String filtername) {
        int pos = 4;
        String[] args = new String[pos + 4];
        args = addAuthParameters(args);
        args[pos++] = "-f";
        args[pos++] = filtername;
        args[pos++] = "-h";
        args[pos++] = reportFilename;

        try {
            BugClerkWithFilterCLI.main(args);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}
