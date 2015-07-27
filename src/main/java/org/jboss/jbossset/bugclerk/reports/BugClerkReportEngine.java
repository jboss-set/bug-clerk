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
import org.jboss.jbossset.bugclerk.utils.FlagsUtils;
import org.jboss.jbossset.bugclerk.utils.URLUtils;
import org.jboss.pull.shared.connectors.bugzilla.Bug;

public final class BugClerkReportEngine implements ReportEngine<BugClerkReport> {


    private final String urlPrefix;

    public BugClerkReportEngine(String urlPrefix) {
        this.urlPrefix = urlPrefix;
    }

    @Override
    public BugClerkReport createReport(Map<Integer, List<Violation>> violationByBugId) {
        BugClerkReport report = new BugClerkReport();
        List<BugReport> bugs = new ArrayList<>(violationByBugId.size());
        for ( Entry<Integer, List<Violation>> entry : violationByBugId.entrySet() ) {
            BugReport bugReport = new BugReport();
            Candidate candidate = entry.getValue().get(0).getCandidate();
            Bug bug = candidate.getBug();
            bugReport.setBugId(bug.getId());
            bugReport.setStatus(bug.getStatus().toString());
            bugReport.setAckFlags(getAckFlags(candidate));
            bugReport.setReleaseFlags(getAndFormatFlag(candidate,FlagsUtils.RELEASE_64Z));
            bugReport.setLink(URLUtils.createURLFromString(urlPrefix + entry.getKey()));
            List<ViolationDescription> violations = new ArrayList<ViolationDescription>(entry.getValue().size());
            for ( Violation violation : entry.getValue() ) {
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

    private String getAndFormatFlag(Candidate candidate, String flag) {
        return FlagsUtils.formatAckFlagWithStatus(candidate.getFlagWithName(flag));
    }

    private String getAckFlags(Candidate candidate) {
        String dev = getAndFormatFlag(candidate,FlagsUtils.DEV_ACK_FLAG);
        String qa = getAndFormatFlag(candidate,FlagsUtils.QA_ACK_FLAG);
        String pm = getAndFormatFlag(candidate,FlagsUtils.PM_ACK_FLAG);
        return dev + "/" + qa + "/" + pm;
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
