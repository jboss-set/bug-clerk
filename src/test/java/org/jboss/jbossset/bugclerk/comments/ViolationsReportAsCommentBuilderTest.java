package org.jboss.jbossset.bugclerk.comments;

import org.jboss.jbossset.bugclerk.MockUtils;
import org.jboss.jbossset.bugclerk.Violation;
import org.jboss.jbossset.bugclerk.checks.AssignedButStillOnSET;
import org.jboss.jbossset.bugclerk.checks.PostMissingPmAck;
import org.jboss.set.aphrodite.domain.Comment;
import org.jboss.set.aphrodite.domain.Issue;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by rpelisse on 12/04/16.
 */
public class ViolationsReportAsCommentBuilderTest {

    @Test
    public void ensureNoDuplicates() {
        ViolationsReportAsCommentBuilder builder = new ViolationsReportAsCommentBuilder();
        final int nbIssue = 3;
        Map<Issue, List<Violation>> violations = new HashMap<Issue, List<Violation>>(nbIssue);
        List<Issue> issues = MockUtils.generateMockIssues(nbIssue, "JBEAP-10", "Issue nb");
        for ( Issue issue : issues )
            violations.put(issue,MockUtils.generateMockViolationsForIssue(issue.getTrackerId().get(),
                    AssignedButStillOnSET.class.getSimpleName(),
                    PostMissingPmAck.class.getSimpleName()));
        System.out.println(violations.size());
        for (Map.Entry<Issue, Comment> entry: builder.reportViolationToBugTracker(violations).entrySet() ) {
            System.out.println(entry.getKey().getTrackerId().get());
            System.out.println(entry.getValue().getBody());
        }
    }


}
