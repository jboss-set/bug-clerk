package org.jboss.jbossset.bugclerk.reports.xml;

import java.net.URL;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class BugReport {

    private String bugId;
    private URL link;
    private String ackFlags;
    private String releaseFlags;
    private String status;

    private List<ViolationDescription> violations;

    /**
     * Empty constructor is required by JAX-B
     */
    public BugReport() { //NOPMD
    }

    public BugReport(String bugId, String status, String ackFlags, URL link, List<ViolationDescription> violations) {
        this.bugId = bugId;
        this.status = status;
        this.ackFlags = ackFlags;
        this.link = link;
        this.violations = violations;
    }

    @XmlAttribute(name = "id")
    public String getBugId() {
        return bugId;
    }

    public void setBugId(String bugId) {
        this.bugId = bugId;
    }

    @XmlAttribute(name = "acks")
    public String getAckFlags() {
        return ackFlags;
    }

    public void setAckFlags(String ackFlags) {
        this.ackFlags = ackFlags;
    }

    @XmlAttribute(name = "release")
    public String getReleaseFlags() {
        return releaseFlags;
    }

    public void setReleaseFlags(String releaseFlags) {
        this.releaseFlags = releaseFlags;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @XmlElement(name = "violation")
    public List<ViolationDescription> getViolations() {
        return violations;
    }

    public void setViolations(List<ViolationDescription> violations) {
        this.violations = violations;
    }

    @XmlAttribute(name = "href")
    public URL getLink() {
        return link;
    }

    public void setLink(URL link) {
        this.link = link;
    }

}
