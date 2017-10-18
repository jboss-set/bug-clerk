/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2015, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.jbossset.bugclerk;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.transform.stream.StreamSource;

import org.jboss.jbossset.bugclerk.aphrodite.AphroditeClient;
import org.jboss.jbossset.bugclerk.comments.ViolationsReportAsCommentBuilder;
import org.jboss.jbossset.bugclerk.reports.BugClerkReportEngine;
import org.jboss.jbossset.bugclerk.reports.ReportEngine;
import org.jboss.jbossset.bugclerk.reports.StringReportEngine;
import org.jboss.jbossset.bugclerk.reports.xml.BugClerkReport;
import org.jboss.jbossset.bugclerk.utils.LoggingUtils;
import org.jboss.jbossset.bugclerk.utils.OutputInputStreamUtils;
import org.jboss.jbossset.bugclerk.utils.XMLUtils;
import org.jboss.set.aphrodite.domain.Issue;
import org.jboss.set.aphrodite.domain.Violation;

public class BugClerk {

    private final AphroditeClient aphrodite;
    private final PerformanceMonitor monitor = new PerformanceMonitor();
    private final BugclerkConfiguration configuration;

    public BugClerk(AphroditeClient aphrodite, BugclerkConfiguration configuration) {
        this.aphrodite = aphrodite;
        this.configuration = configuration;
    }

    protected Collection<Candidate> processEntriesAndReportViolations(List<Candidate> candidates) {
        return processEntriesAndReportViolations(candidates, configuration.getChecknames());
    }

    protected Collection<Candidate> processEntriesAndReportViolations(List<Candidate> candidates, Collection<String> checknames) {
        RulesEngine ruleEngine = new RulesEngine(new HashMap<String, Object>(0), this.aphrodite);
        Collection<Candidate> violations = ruleEngine.runChecksOnBugs(candidates, checknames);
        ruleEngine.shutdownRuleEngine();
        return violations;
    }

    public List<Violation> getViolationsOnIssue(Issue issue, Collection<String> cheknames) {
        Candidate candidate = new Candidate(issue);
        Collection<Candidate> canditates = processEntriesAndReportViolations(Arrays.asList(candidate), cheknames);
        List<Violation> violations = new ArrayList<Violation>(0);
        for ( Candidate checkedIssue : canditates ) {
            if ( ! checkedIssue.getViolations().isEmpty() && checkedIssue.getBug().getTrackerId().equals(issue.getTrackerId()) )
                violations.addAll(checkedIssue.getViolations());
        }
        return violations;
    }

    protected String buildReport(Collection<Candidate> candidates) {
        ReportEngine<String> reportEngine = new StringReportEngine();
        return reportEngine.createReport(candidates);
    }

    protected BugClerkReport buildBugClerkReport(Collection<Candidate> violationByBugId) {
        ReportEngine<BugClerkReport> reportEngine = new BugClerkReportEngine();
        return reportEngine.createReport(violationByBugId);
    }

    public int runAndReturnsViolations(List<Issue> ids) {
        LoggingUtils.configureLogger(configuration.isDebug());

        LoggingUtils.getLogger().info("Loading data for " + ids.size() + " issues.");
        List<Candidate> candidates = ids.parallelStream().map(issue -> new Candidate(issue))
                .collect(Collectors.toList());
        LoggingUtils.getLogger().info("Loading data from tracker took:" + monitor.returnsTimeElapsedAndRestartClock() + "s.");

        Collection<Candidate> violations = processEntriesAndReportViolations(candidates);
        int nbViolations = 0;
        for ( Candidate candidate : violations ) {
            if ( ! candidate.getViolations().isEmpty() ) {
             LoggingUtils.getLogger().fine("Issue:" + candidate.getBug().getTrackerId().get() + " has " + candidate.getViolations().size() );
             nbViolations += candidate.getViolations().size();
            }
        }
        LoggingUtils.getLogger().info("Found " + nbViolations + " violations:");
        String report = buildReport(violations);

        LoggingUtils.getLogger().fine("Report produced, running post analysis actions");
        postAnalysisActions(violations, report);

        LoggingUtils.getLogger().fine("Analysis took:" + monitor.returnsTimeElapsedAndRestartClock() + "s.");
        LoggingUtils.getLogger().info(report);

        reportsGeneration(violations);
        LoggingUtils.getLogger().fine("Generating XML/HTML Report:" + monitor.returnsTimeElapsedAndRestartClock() + "s.");
        return nbViolations;
    }

    protected void reportsGeneration(Collection<Candidate> violationByBugId) {
        if (configuration.isXMLReport() || configuration.isHtmlReport()) {
            BugClerkReport xmlReport = buildBugClerkReport(violationByBugId);
            if (configuration.isXMLReport())
                BugClerkReportEngine.printXmlReport(xmlReport,
                        OutputInputStreamUtils.getOutputStreamForFile(configuration.getXmlReportFilename()));
            if (configuration.isHtmlReport())
                XMLUtils.xmlToXhtml(xmlReport,
                        new StreamSource(this.getClass().getResourceAsStream(BugClerkReportEngine.XSLT_FILENAME)),
                        OutputInputStreamUtils.getStreamResultForFile(configuration.getHtmlReportFilename()));
        }
    }

    protected String getXmlReportFilename() {
        return configuration.getXmlReportFilename() == null ? configuration.getHtmlReportFilename() + ".xml" : configuration.getXmlReportFilename();
    }

    protected void postAnalysisActions(Collection<Candidate> candidates, String report) {
        if (! candidates.isEmpty() && configuration.isReportViolation()) {
            LoggingUtils.getLogger().info("Updating Bugzilla entries - if needed.");
            aphrodite.addComments(new ViolationsReportAsCommentBuilder().reportViolationToBugTracker(candidates));
            LoggingUtils.getLogger().info("Bugzilla entries updated - if needed.");
        }
    }
}
