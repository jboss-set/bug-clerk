package org.jboss.jbossset.bugclerk.utils;

import java.util.Optional;

import org.jboss.set.aphrodite.domain.User;

/**
 * Utility class to regroup handy methods around the {@link User} class.
 *
 * @author Romain Pelisse <belaran@redhat.com>
 *
 */
public final class UserUtil {

    private UserUtil() {
    }

    /**
     * Return true is the {@link User} has been set, either by email address or by name.
     * @param user
     * @return true if either the user's name or email has been set to a not empty {@link String}.
     */
    public static boolean isUserSet(User user) {
        return userHasEmail(user) || userHasName(user);
    }

    /**
     * Return true if the provided {@link User} has been associated to not empty email ({@link String})
     * @param user
     * @return true if the email has been defined to a not empty {@link String}.
     */
    public static boolean userHasEmail(User user) {
        return (user != null && user.getEmail() != null && user.getEmail().isPresent() && !"".equals(user.getEmail().get()));
    }

    /**
     * Return true if the provided {@link User} has been associated to a not empty name ({@link String})
     * @param user
     * @return true if user's name has been set
     */
    public static boolean userHasName(User user) {
        return (user != null && isStringEmpty(user.getName()));
    }

    private static boolean isStringEmpty(Optional<String> subject) {
        return (subject != null && subject.isPresent() && ! subject.get().equals(""));
    }
}
