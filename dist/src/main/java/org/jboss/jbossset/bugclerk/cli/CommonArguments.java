package org.jboss.jbossset.bugclerk.cli;

import com.beust.jcommander.Parameter;

public class CommonArguments {

    public static final String APHRODITE_CONFIG = "APHRODITE_CONFIG";

    @Parameter(names = { "-?", "--help" }, description = "print help text", required = false)
    private boolean help = false;

    @Parameter(names = { "-d", "--debug" }, description = "debug mode", required = false)
    private boolean debug = false;

    @Parameter(names = { "-x", "--xml-file" }, description = "Filepath to store the report as XML - no report is generated if option omitted", required = false)
    private String xmlReportFilename;

    @Parameter(names = { "-h", "--html-report" }, description = "Create an html report, on top of the XML one", required = false)
    private String htmlReportFilename;

    @Parameter(names = { "-c", "--comment-on-bz" }, description = "add a comment to a BZ featuring violations, default is false", required = false)
    private boolean reportToBz = false;

    @Parameter(names = { "-F", "--fail-on-violation" }, description = "exit program with status equals to number of violations", required = false)
    private boolean failOnViolation = false;

    @Parameter(names = { "-r","--rules" }, description = "Checks, by names - separated by comma, to enable for the run", required = false)
    private String checknames = "";

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

    public String getXmlReportFilename() {
        return xmlReportFilename;
    }

    public void setXmlReportFilename(String xmlReportFilename) {
        this.xmlReportFilename = xmlReportFilename;
    }

    public String getHtmlReportFilename() {
        return htmlReportFilename;
    }

    public void setHtmlReportFilename(String htmlReportFilename) {
        this.htmlReportFilename = htmlReportFilename;
    }

    public boolean isReportToBz() {
        return reportToBz;
    }

    public void setReportToBz(boolean reportToBz) {
        this.reportToBz = reportToBz;
    }

    public boolean isFailOnViolation() {
        return failOnViolation;
    }

    public void setFailOnViolation(boolean isFailOnViolation) {
        this.failOnViolation = isFailOnViolation;
    }

    public String getChecknames() {
        return checknames;
    }

    public void setChecknames(String checknames) {
        this.checknames = checknames;
    }

    @Override
    public String toString() {
        return "CommonArguments [help=" + help + ", debug=" + debug + ", xmlReportFilename=" + xmlReportFilename
                + ", htmlReportFilename=" + htmlReportFilename + ", reportToBz=" + reportToBz + ", failOnViolation="
                + failOnViolation + ", checknames=" + checknames + "]";
    }

}
