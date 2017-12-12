package org.jboss.jbossset.bugclerk.comments;

import org.jboss.jbossset.bugclerk.Candidate;
import org.jboss.jbossset.bugclerk.MockUtils;
import org.jboss.jbossset.bugclerk.Violation;
import org.jboss.jbossset.bugclerk.checks.AssignedButStillOnSET;
import org.jboss.jbossset.bugclerk.checks.PostMissingPR;
import org.jboss.jbossset.bugclerk.checks.PostMissingPmAck;
import org.jboss.jbossset.bugclerk.utils.StringUtils;
import org.jboss.set.aphrodite.domain.Comment;
import org.jboss.set.aphrodite.domain.Issue;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by rpelisse on 12/04/16.
 */
public class ViolationsReportAsCommentBuilderTest {

    private ViolationsReportAsCommentBuilder builder;

    @Before
    public void buildTestInstance() {
        builder = new ViolationsReportAsCommentBuilder();
    }

    @Test
    public void ensureNoDuplicates() {

        // Prepare mock data
        final int nbIssue = 3;
        final String[] checknames = { AssignedButStillOnSET.class.getSimpleName(), PostMissingPmAck.class.getSimpleName()};
        Map<Issue, List<Violation>> violations = new HashMap<Issue, List<Violation>>(nbIssue);
        List<Issue> issues = MockUtils.generateMockIssues(nbIssue, "JBEAP-10", "Issue nb");
        for ( Issue issue : issues )
            violations.put(issue,MockUtils.generateMockViolationsForIssue(issue.getTrackerId().get(), checknames));
        // Run and asserts
        Collection<Candidate> candidates = new ArrayList<Candidate>(1);
        Candidate candidate = new Candidate(MockUtils.mockBzIssue("mockId", "summary"));
        candidates.add(candidate);
        for (Map.Entry<Issue, Comment> entry: builder.reportViolationToBugTracker(candidates).entrySet() ) {
            for ( String checkname : checknames ) {
                final int nbOccurences = StringUtils.occurencesInString(StringUtils.formatCheckname(checkname), entry.getValue().getBody());
                if (nbOccurences > 1)
                    fail("Checkname " + checkname + " appears more than once in comment body:\n" + entry.getValue().getBody());
            }
        }
    }

    @Test
    public void ensureNoNewCommentAdded() {
        final String checkname = AssignedButStillOnSET.class.getSimpleName();
        final String issueId = "JBEAP-666";
        Comment comment = MockUtils.mockComment("10", StringUtils.formatCheckname(checkname), issueId);
        Violation violation = MockUtils.mockViolation(issueId,checkname);
        List<Violation> violationsToReport = new ArrayList<Violation>(0);
        builder.addViolationToReportIfNotAlreadyReported(checkname,Arrays.asList(comment),violationsToReport,violation);
        assertTrue(violationsToReport.isEmpty());
    }


    @Test
    public void ensureNewCommentAddedIfChecknameDiffers() {
        final String checkname = AssignedButStillOnSET.class.getSimpleName();
        final String issueId = "JBEAP-666";
        Comment comment = MockUtils.mockComment("10", StringUtils.formatCheckname(PostMissingPR.class.getSimpleName()), issueId);
        Violation violation = MockUtils.mockViolation(issueId,checkname);
        List<Violation> violationsToReport = new ArrayList<Violation>(0);
        builder.addViolationToReportIfNotAlreadyReported(checkname,Arrays.asList(comment),violationsToReport,violation);
        assertTrue(violationsToReport.size() == 1);
    }
}
