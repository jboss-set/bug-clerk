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

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.xml.transform.stream.StreamSource;

import org.jboss.jbossset.bugclerk.aphrodite.AphroditeClient;
import org.jboss.jbossset.bugclerk.cli.BugClerkArguments;
import org.jboss.jbossset.bugclerk.comments.ViolationsReportAsCommentBuilder;
import org.jboss.jbossset.bugclerk.reports.BugClerkReportEngine;
import org.jboss.jbossset.bugclerk.reports.ReportEngine;
import org.jboss.jbossset.bugclerk.reports.StringReportEngine;
import org.jboss.jbossset.bugclerk.reports.xml.BugClerkReport;
import org.jboss.jbossset.bugclerk.utils.LoggingUtils;
import org.jboss.jbossset.bugclerk.utils.StreamUtils;
import org.jboss.jbossset.bugclerk.utils.XMLUtils;
import org.jboss.set.aphrodite.domain.Issue;

public class BugClerk {

    private final AphroditeClient aphrodite;
    private final PerformanceMonitor monitor = new PerformanceMonitor();

    public BugClerk(AphroditeClient aphrodite) {
        this.aphrodite = aphrodite;
    }

    protected Collection<Violation> processEntriesAndReportViolations(List<Candidate> candidates) {
        RuleEngine ruleEngine = new RuleEngine(buildGlobalsMap());
        Collection<Violation> violations = ruleEngine.processBugEntry(candidates);
        ruleEngine.shutdownRuleEngine();
        return violations;
    }

    protected Map<String, Object> buildGlobalsMap() {
        Map<String, Object> globalsMap = new HashMap<String, Object>(1);
        return globalsMap;
    }

    protected String buildReport(Map<Issue, List<Violation>> violationByBugId) {
        ReportEngine<String> reportEngine = new StringReportEngine();
        return reportEngine.createReport(violationByBugId);
    }

    protected BugClerkReport buildBugClerkReport(Map<Issue, List<Violation>> violationByBugId) {
        ReportEngine<BugClerkReport> reportEngine = new BugClerkReportEngine();
        return reportEngine.createReport(violationByBugId);
    }

    public int runAndReturnsViolations(BugClerkArguments arguments) {
        LoggingUtils.configureLogger(arguments.isDebug());

        LoggingUtils.getLogger().info("Loading data for " + arguments.getIds().size() + " issues.");
        List<Candidate> candidates = arguments.getIssues().parallelStream().map(issue -> new Candidate(issue))
                .collect(Collectors.toList());
        LoggingUtils.getLogger().info("Loading data from tracker took:" + monitor.returnsTimeElapsedAndRestartClock() + "s.");

        Collection<Violation> violations = processEntriesAndReportViolations(candidates);
        Map<Issue, List<Violation>> violationByBugId = violations.stream().collect(
                Collectors.groupingBy(v -> v.getCandidate().getBug()));
        LoggingUtils.getLogger().info("Found " + violations.size() + " violations:");
        String report = buildReport(violationByBugId);

        LoggingUtils.getLogger().fine("Report produced, running post analysis actions");
        postAnalysisActions(arguments, violationByBugId, report);

        LoggingUtils.getLogger().fine("Analysis took:" + monitor.returnsTimeElapsedAndRestartClock() + "s.");
        LoggingUtils.getLogger().info(report);

        reportsGeneration(arguments, violationByBugId);
        LoggingUtils.getLogger().fine("Generating XML/HTML Report:" + monitor.returnsTimeElapsedAndRestartClock() + "s.");
        return violationByBugId.size();
    }

    protected void reportsGeneration(BugClerkArguments arguments, Map<Issue, List<Violation>> violationByBugId) {
        if (arguments.isXMLReport() || arguments.isHtmlReport()) {
            BugClerkReport xmlReport = buildBugClerkReport(violationByBugId);
            if (arguments.isXMLReport())
                BugClerkReportEngine.printXmlReport(xmlReport,
                        StreamUtils.getOutputStreamForFile(arguments.getXmlReportFilename()));
            if (arguments.isHtmlReport())
                XMLUtils.xmlToXhtml(xmlReport,
                        new StreamSource(this.getClass().getResourceAsStream(BugClerkReportEngine.XSLT_FILENAME)),
                        StreamUtils.getStreamResultForFile(arguments.getHtmlReportFilename()));
        }
    }

    protected static String getXmlReportFilename(BugClerkArguments arguments) {
        return arguments.getXmlReportFilename() != null ? arguments.getXmlReportFilename() : arguments.getHtmlReportFilename()
                + ".xml";
    }

    protected void postAnalysisActions(BugClerkArguments arguments, Map<Issue, List<Violation>> violationByBugId, String report) {
        if (!violationByBugId.isEmpty() && arguments.isReportToBz()) {
            LoggingUtils.getLogger().info("Updating Bugzilla entries - if needed.");
            aphrodite.addComments(new ViolationsReportAsCommentBuilder().reportViolationToBugTracker(violationByBugId));
            LoggingUtils.getLogger().info("Bugzilla entries updated - if needed.");
        }
    }
}