package org.jboss.jbossset.bugclerk;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import org.jboss.jbossset.bugclerk.bugzilla.ParallelLoader;
import org.jboss.jbossset.bugclerk.cli.BugClerkArguments;
import org.jboss.jbossset.bugclerk.smtp.SMTPClient;
import org.jboss.jbossset.bugclerk.utils.LoggingUtils;

public class BugClerk {

    private final PerformanceMonitor monitor = new PerformanceMonitor();

    protected List<Candidate> loadCandidates(List<String> ids) {
        return new ParallelLoader().loadCandidates(ids);
    }

    static final String KIE_SESSION = "BzCheck";

    protected Collection<Violation> processEntriesAndReportViolations(List<Candidate> candidates) {
        RuleEngine ruleEngine = new RuleEngine(KIE_SESSION);
        Collection<Violation> violations = ruleEngine.processBugEntry(candidates);
        ruleEngine.shutdownRuleEngine();
        return violations;
    }

    protected String buildReport(Collection<Violation> violations, String urlPrefix) {
        ReportEngine reportEngine = new ReportEngine(urlPrefix);
        return reportEngine.createReport(violations);
    }

    private static final String TO = "rpelisse@redhat.com";
    private static final String FROM = "BugClerk <rpelisse@redhat.com>";

    protected void publishReport(String report) {
        String subject = "BugClerk Report - " + new SimpleDateFormat("yyyy/MM/dd - HH:mm").format(Calendar.getInstance().getTime());
        new SMTPClient().sendEmail(TO, FROM , subject, report);
    }

    public void run(BugClerkArguments arguments) {
        LoggingUtils.configureLogger(arguments.isDebug());

        LoggingUtils.getLogger().info("Loading data for " + arguments.getIds().size() + " issues.");
        List<Candidate> candidates = loadCandidates(arguments.getIds());
        LoggingUtils.getLogger().info("Loading data from tracker took:" + monitor.returnsTimeElapsedAndRestartClock() + "s.");

        Collection<Violation> violations = processEntriesAndReportViolations(candidates);
        LoggingUtils.getLogger().info("Found " + violations.size() + " violations:");
        String report = buildReport(violations, arguments.getUrlPrefix());

        LoggingUtils.getLogger().fine("Analysis took:" + monitor.returnsTimeElapsedAndRestartClock() + "s.");
        LoggingUtils.getLogger().info(report);

        publishReport(report);
    }
}
