package org.jboss.jbossset.bugclerk.reports;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.jboss.jbossset.bugclerk.Candidate;
import org.jboss.jbossset.bugclerk.Violation;
import org.jboss.jbossset.bugclerk.reports.xml.BugClerkReport;
import org.jboss.jbossset.bugclerk.reports.xml.BugReport;
import org.jboss.jbossset.bugclerk.reports.xml.ViolationDescription;
import org.jboss.jbossset.bugclerk.utils.URLUtils;
import org.jboss.set.aphrodite.domain.Flag;
import org.jboss.set.aphrodite.domain.Issue;

public final class BugClerkReportEngine implements ReportEngine<BugClerkReport> {

    private final String urlPrefix;

    public static final String XSLT_FILENAME = "/xslt/stylesheet.xsl";

    public BugClerkReportEngine(String urlPrefix) {
        this.urlPrefix = urlPrefix;
    }

    @Override
    public BugClerkReport createReport(Map<String, List<Violation>> violationByBugId) {
        BugClerkReport report = new BugClerkReport();
        List<BugReport> bugs = new ArrayList<>(violationByBugId.size());
        for (Entry<String, List<Violation>> entry : violationByBugId.entrySet()) {
            BugReport bugReport = new BugReport();
            Candidate candidate = entry.getValue().get(0).getCandidate();
            Issue bug = candidate.getBug();
            bugReport.setBugId(bug.getTrackerId().get());
            bugReport.setStatus(bug.getStatus().toString());
            bugReport.setAckFlags(getAckFlags(candidate));
            bugReport.setLink(URLUtils.createURLFromString(urlPrefix + entry.getKey()));
            List<ViolationDescription> violations = new ArrayList<ViolationDescription>(entry.getValue().size());
            for (Violation violation : entry.getValue()) {
                ViolationDescription desc = new ViolationDescription();
                desc.setCheckname(violation.getCheckName());
                desc.setMessage(violation.getMessage());
                desc.setSeverity(violation.getLevel().toString());
                violations.add(desc);
            }
            bugReport.setViolations(violations);
            bugs.add(bugReport);
        }
        report.setBugs(bugs);
        return report;
    }

    private String getAckFlags(Candidate candidate) {
        return candidate.getBug().getStage().getStatus(Flag.DEV) + "/" + candidate.getBug().getStage().getStatus(Flag.QE) + "/"
                + candidate.getBug().getStage().getStatus(Flag.PM);
    }

    public static void printXmlReport(BugClerkReport report, OutputStream out) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(BugClerkReport.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

            // output pretty printed
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            jaxbMarshaller.marshal(report, out);
        } catch (JAXBException e) {
            throw new IllegalStateException(e);
        }
    }

}
