package org.jboss.jbossset.bugclerk.cli;

import com.beust.jcommander.Parameter;

public abstract class AbstractCommonArguments {

    @Parameter(names = { "-?", "--help" }, description = "print help text", required = false)
    private boolean help = false;

    @Parameter(names = { "-d", "--debug" }, description = "debug mode", required = false)
    private boolean debug = false;

    @Parameter(names = { "-x", "--xml-file" }, description = "Filepath to store the report as XML - no report is generated if option omitted", required = false)
    private String xmlReportFilename;

    @Parameter(names = { "-h", "--html-report" }, description = "Create an html report, on top of the XML one", required = false)
    private String htmlReportFilename;

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

    public boolean isXMLReport() {
        return ! (this.xmlReportFilename == null || "".equals(this.xmlReportFilename));
    }

    public boolean isHtmlReport() {
        return ! (this.htmlReportFilename == null || "".equals(this.htmlReportFilename));
    }

    @Override
    public String toString() {
        return "AbstractCommonArguments [help=" + help + ", debug=" + debug + ", xmlReportFilename=" + xmlReportFilename
                + ", htmlReportFilename=" + htmlReportFilename + "]";
    }


}
