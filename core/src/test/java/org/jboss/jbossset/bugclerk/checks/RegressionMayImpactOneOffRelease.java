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
package org.jboss.jbossset.bugclerk.checks;

import static org.jboss.jbossset.bugclerk.checks.utils.AssertsHelper.BUGZILLA_TRACKER_ID_PREFIX;
import static org.jboss.jbossset.bugclerk.checks.utils.AssertsHelper.assertNoViolationFound;
import static org.jboss.jbossset.bugclerk.checks.utils.AssertsHelper.assertOneViolationFound;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.jboss.jbossset.bugclerk.AbstractCheckRunner;
import org.jboss.jbossset.bugclerk.Candidate;
import org.jboss.jbossset.bugclerk.MockUtils;
import org.jboss.set.aphrodite.domain.Comment;
import org.jboss.set.aphrodite.domain.Issue;
import org.jboss.set.aphrodite.domain.IssueType;
import org.junit.Test;
import org.mockito.Mockito;

public class RegressionMayImpactOneOffRelease extends AbstractCheckRunner {

    private final String originalBugId = "143794";
    private final String oneOffBugId = "143798";

    @Test
    public void oneOffBZWithRegressionInBlocksList() {
        assertOneViolationFound(engine.runCheckOnBugs(prepareCandidates("Regression here..."), checkName), checkName,
                originalBugId);
    }

    @Test
    public void oneOffBZWithRegressionLowerCaseInBlocksList() {
        assertOneViolationFound(engine.runCheckOnBugs(prepareCandidates("Appears to be a regression here..."), checkName),
                checkName, originalBugId);
    }

    public void oneOffButNoRegression() {
        assertNoViolationFound(engine.runCheckOnBugs(prepareCandidates("New bug ?"), checkName), checkName, oneOffBugId);
    }

    protected Collection<Candidate> prepareCandidates(String regressionText) {
        Collection<Candidate> candidates = new HashSet<Candidate>(2);
        candidates.add(prepareOneOff());
        candidates.add(prepareOriginal(regressionText));
        return candidates;
    }

    protected Candidate prepareOneOff() {
        final Issue oneOff = MockUtils.mockBzIssue(oneOffBugId, "it's a one-off");
        Mockito.when(oneOff.getType()).thenReturn(IssueType.ONE_OFF);
        URL url = buildUrlForId(originalBugId);
        URL url2 = buildUrlForId(originalBugId);
        assert url.equals(url2);
        List<URL> urls = buildUrlListForOneUrl(url);
        assert urls.contains(url);
        Mockito.when(oneOff.getBlocks()).thenReturn(buildUrlListForOneUrl(buildUrlForId(originalBugId)));
        assert !oneOff.getBlocks().isEmpty();
        return new Candidate(oneOff);
    }

    private List<URL> buildUrlListForOneUrl(URL url) {
        List<URL> list = new ArrayList<URL>(1);
        list.add(buildUrlForId(originalBugId));
        return list;
    }

    private URL buildUrlForId(String id) {
        try {
            return new URL(BUGZILLA_TRACKER_ID_PREFIX + id);
        } catch (MalformedURLException e) {
            throw new IllegalStateException(e);
        }
    }

    protected Candidate prepareOriginal(String regressionText) {
        final List<Comment> comments = MockUtils.mockCommentsWithOneItem("1", regressionText, originalBugId);
        final Issue originalOne = MockUtils.mockBzIssue(originalBugId, "it's the original one");
        Mockito.when(originalOne.getDependsOn()).thenReturn(buildUrlListForOneUrl(buildUrlForId(oneOffBugId)));
        Mockito.when(originalOne.getType()).thenReturn(IssueType.SUPPORT_PATCH);
        Mockito.when(originalOne.getComments()).thenReturn(comments);
        return new Candidate(originalOne);
    }
}