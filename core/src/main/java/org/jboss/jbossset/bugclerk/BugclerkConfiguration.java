package org.jboss.jbossset.bugclerk;



public class BugclerkConfiguration {

    private boolean debug = false;

    private String xmlReportFilename;

    private String htmlReportFilename;

    private boolean reportToBz = false;

    private boolean failOnViolation = false;

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
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

    public void setFailOnViolation(boolean failOnViolation) {
        this.failOnViolation = failOnViolation;
    }

    public boolean isXMLReport() {
        return !(this.xmlReportFilename == null || "".equals(this.xmlReportFilename));
    }

    public boolean isHtmlReport() {
        return !(this.htmlReportFilename == null || "".equals(this.htmlReportFilename));
    }
}
