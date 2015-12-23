package org.jboss.jbossset.bugclerk.cli;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class BugClerkIntegrationTests {

    private final static String BZ_SERVER_CREDENTIALS_FILE = "bugclerk.properties";
    private final static String REDHAT_BZ_SERVER_URL = "https://bugzilla.redhat.com";
    private final static String REDHAT_BZ_SERVER_INDEX = REDHAT_BZ_SERVER_URL + "/index.cgi";
    private final static String URL_TO_REDHAT_BZ = REDHAT_BZ_SERVER_URL + "/show_bug.cgi?id=";

    private String reportFilename;

    @BeforeClass
    public static void checkIfCredentialFileExists() {
        File credential = new File(BZ_SERVER_CREDENTIALS_FILE);
        if (credential == null || !credential.exists())
            throw new IllegalStateException("No credential file:" + BZ_SERVER_CREDENTIALS_FILE);
        if (!credential.canRead())
            throw new IllegalStateException("Credential file exists, but can't be read:" + BZ_SERVER_CREDENTIALS_FILE);
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
        final String[] firstSetOfArgs = { "1187026", "1187027", "1185118", "-u", URL_TO_REDHAT_BZ };
        runBugClerk(firstSetOfArgs);
        final String[] secondSetOfArgs = { "1039989", "1039989", "1185118", "-u", URL_TO_REDHAT_BZ };
        runBugClerk(secondSetOfArgs);
    }

    @Test
    public void runOnBZ1203181() {
        String[] args = { "1203181", "-u", URL_TO_REDHAT_BZ };
        runBugClerk(args);
    }

    @Test
    public void runOnClosedBZ() {
        String[] args = { "1199194", "-u", URL_TO_REDHAT_BZ };
        runBugClerk(args);
    }

    private static void runBugClerkWithFiltername(String reportFilename, String filtername) {
        String[] args = { "-H", REDHAT_BZ_SERVER_INDEX, "-h", reportFilename, "-f", filtername };
        try {
            BugClerkWithFilterCLI.main(args);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

}
