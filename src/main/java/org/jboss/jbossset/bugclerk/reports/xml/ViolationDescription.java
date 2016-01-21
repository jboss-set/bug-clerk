package org.jboss.jbossset.bugclerk.reports.xml;

import javax.xml.bind.annotation.XmlAttribute;

public class ViolationDescription {

    private String checkname;
    private String message;
    private String severity;

    public ViolationDescription() {
    }

    public ViolationDescription(String checkname, String message, String severity) {
        this.checkname = checkname;
        this.message = message;
        this.severity = severity;
    }

    @XmlAttribute
    public String getCheckname() {
        return checkname;
    }

    public void setCheckname(String checkname) {
        this.checkname = checkname;
    }

    @XmlAttribute
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @XmlAttribute
    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }
}
