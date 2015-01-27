package org.jboss.jbossset.bugclerk;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import org.jboss.pull.shared.connectors.bugzilla.Bug;
import org.jboss.pull.shared.connectors.bugzilla.Comment;

public class BugClerk  {

    private final PerformanceMonitor monitor = new PerformanceMonitor();

    @SuppressWarnings("unchecked")
    protected List<Candidate> loadCandidates(List<String> ids) {
        Map<String, SortedSet<Comment>> commentsByBugId = BzUtils.loadCommentForBug(ids);
        Map<String, Bug> bugsById = BzUtils.loadBugsById(new HashSet<String>(ids));
        List<Candidate> candidates = new ArrayList<Candidate>(ids.size());
        for ( Bug bug : bugsById.values() )
            candidates.add(new Candidate(bug, CollectionUtils.getEntryOrEmptySet(String.valueOf(bug.getId()),commentsByBugId) ));
        return candidates;
    }

    private static final String KIE_SESSION = "BzCheck";

    protected Collection<Violation> processEntriesAndReportViolations(List<Candidate> candidates) {
        Object[] facts = { candidates };
        RuleEngine ruleEngine = new RuleEngine(KIE_SESSION);
        Collection<Violation> violations = ruleEngine.processBugEntry(facts);
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

    public void run(Arguments arguments) {
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
