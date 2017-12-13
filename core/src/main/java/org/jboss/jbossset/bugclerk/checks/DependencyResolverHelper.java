package org.jboss.jbossset.bugclerk.checks;

import java.net.URL;
import java.util.List;

import org.jboss.jbossset.bugclerk.aphrodite.AphroditeClient;
import org.jboss.set.aphrodite.domain.IssueStatus;
import org.jboss.set.aphrodite.domain.IssueType;
/**
 * <p>Regroups a set of static method used by some checks.</p>
 *
 * @author Romain Pelisse - belaran@redhat.com
 *
 */
public final class DependencyResolverHelper {

    private DependencyResolverHelper(){}

    /**
     * Returns false if *any* of the dependencies is NOT in the provided status.
     *
     * @param dependencies - list of issues (the dependencies)
     * @param status - the status issues are expected to be in
     * @param aphrodite - the aphrodite client
     * @return
     */
    public static boolean checksDependencyStatus(List<URL> dependencies, IssueStatus status, AphroditeClient aphrodite) {
        return aphrodite.loadIssuesFromUrls(dependencies).stream().
                            filter(issue -> ! issue.getStatus().equals(status)).findAny().isPresent();
    }

    public static boolean checksIfDependencyIsAComponentUpgrade(List<URL> dependencies, AphroditeClient aphrodite) {
        return aphrodite.loadIssuesFromUrls(dependencies).stream().
                            filter(issue -> issue.getType().equals(IssueType.UPGRADE)).findAny().isPresent();
    }
}
