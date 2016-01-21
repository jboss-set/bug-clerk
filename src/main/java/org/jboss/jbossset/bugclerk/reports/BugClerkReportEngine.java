package org.jboss.jbossset.bugclerk.reports;

import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.jboss.jbossset.bugclerk.Candidate;
import org.jboss.jbossset.bugclerk.Violation;
import org.jboss.jbossset.bugclerk.reports.xml.BugClerkReport;
import org.jboss.jbossset.bugclerk.reports.xml.BugReport;
import org.jboss.jbossset.bugclerk.reports.xml.ViolationDescription;
import org.jboss.set.aphrodite.domain.Flag;
import org.jboss.set.aphrodite.domain.Issue;

public final class BugClerkReportEngine implements ReportEngine<BugClerkReport> {

    public static final String XSLT_FILENAME = "/xslt/stylesheet.xsl";

    @Override
    public BugClerkReport createReport(Map<Issue, List<Violation>> violationByBugId) {
        return new BugClerkReport(createBugReports(violationByBugId.entrySet()));
    }

    private static List<BugReport> createBugReports(Set<Entry<Issue, List<Violation>>> entries) {
        return entries.parallelStream().map(v -> createBugReport(v)).collect(Collectors.toList());
    }

    private static BugReport createBugReport(Entry<Issue, List<Violation>> entry) {
        return new BugReport(entry.getKey().getTrackerId().get(), entry.getKey().getStatus().toString(), getAckFlags(entry
                .getValue().get(0).getCandidate()), entry.getKey().getURL(), buildViolationsList(entry.getValue()));
    }

    private static List<ViolationDescription> buildViolationsList(List<Violation> violations) {
        return violations.parallelStream()
                .map(v -> new ViolationDescription(v.getCheckName(), v.getMessage(), v.getLevel().toString()))
                .collect(Collectors.toList());
    }

    private static String getAckFlags(Candidate candidate) {
        return candidate.getBug().getStage().getStatus(Flag.DEV) + "/" + candidate.getBug().getStage().getStatus(Flag.QE) + "/"
                + candidate.getBug().getStage().getStatus(Flag.PM);
    }

    private static Marshaller createJaxbMarshaller(JAXBContext jaxbContext) throws JAXBException {
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        return jaxbMarshaller;
    }

    public static void printXmlReport(BugClerkReport report, OutputStream out) {
        try {
            createJaxbMarshaller(JAXBContext.newInstance(BugClerkReport.class)).marshal(report, out);
        } catch (JAXBException e) {
            throw new IllegalStateException(e);
        }
    }

}
