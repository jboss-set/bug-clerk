package org.jboss.jbossset.bugclerk.reports;

import java.net.URL;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class BugReport {

    private int bugId;
    private URL link;
    private String ackFlags;
    private String releaseFlags;
    private String status;

    private List<ViolationDescription> violations;

    public BugReport() {} // required by JAXB

    @XmlAttribute(name="id")
    public int getBugId() {
        return bugId;
    }

    public void setBugId(int bugId) {
        this.bugId = bugId;
    }

    @XmlAttribute(name="acks")
    public String getAckFlags() {
        return ackFlags;
    }

    public void setAckFlags(String ackFlags) {
        this.ackFlags = ackFlags;
    }

    @XmlAttribute(name="release")
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

    @XmlElement(name="violation")
    public List<ViolationDescription> getViolations() {
        return violations;
    }

    public void setViolations(List<ViolationDescription> violations) {
        this.violations = violations;
    }

    @XmlAttribute(name="href")
    public URL getLink() {
        return link;
    }

    public void setLink(URL link) {
        this.link = link;
    }


}
