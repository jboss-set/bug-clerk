package org.jboss.jbossset.bugclerk.reports;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.jboss.jbossset.bugclerk.Candidate;
import org.jboss.jbossset.bugclerk.reports.xml.BugClerkReport;
import org.jboss.jbossset.bugclerk.reports.xml.BugReport;
import org.jboss.jbossset.bugclerk.reports.xml.ViolationDescription;
import org.jboss.set.aphrodite.domain.Flag;
import org.jboss.set.aphrodite.domain.Violation;

public final class BugClerkReportEngine implements ReportEngine<BugClerkReport> {

    public static final String XSLT_FILENAME = "/xslt/stylesheet.xsl";

    @Override
    public BugClerkReport createReport(Collection<Candidate> candidates) {
        return new BugClerkReport(createBugReports(candidates));
    }

    private static List<BugReport> createBugReports(Collection<Candidate> entries) {
        List<BugReport> list = new ArrayList<BugReport>(entries.size());
        entries.stream().forEach(c -> {
            if (!c.getViolations().isEmpty())
                list.add(createBugReportCandidate(c));
        });
        return list;
    }

    private static BugReport createBugReportCandidate(Candidate entry) {
        return new BugReport(entry.getBug().getTrackerId().get(), entry.getBug().getStatus().toString(),
                getAckFlags(entry), entry.getBug().getURL(), buildViolationsList(entry.getViolations()));
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
