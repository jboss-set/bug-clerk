/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2017, Red Hat, Inc., and individual contributors
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

package org.jboss.jbossset.bugclerk.utils;

import org.jboss.set.aphrodite.domain.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Marek Marusic <mmarusic@redhat.com> on 6/9/17.
 */
/* This is thread safe lazy loaded singleton,
which contains white list of users who can modify jira issues fix versions.*/
public class WhiteListSingleton {
    private Map<String, List<User>> sprintWhiteList;

    private WhiteListSingleton() {
        sprintWhiteList = new HashMap<>();
        parseSprintWhiteListInput();
    }

    private void parseSprintWhiteListInput() {
        List<User> allowedUsers = new ArrayList<>();
        allowedUsers.add(User.createWithUsername("GSS"));
        allowedUsers.add(User.createWithUsername("ReleaseCoordinator"));
        sprintWhiteList.put("EAP 7.0.3", allowedUsers);
    }

    private static class SingletonHelper {
        private static final WhiteListSingleton INSTANCE = new WhiteListSingleton();
    }

    public static WhiteListSingleton getInstance() {
        return SingletonHelper.INSTANCE;
    }

    public boolean isInSprintWhiteList(User user, String sprint) {
        if (! sprintWhiteList.containsKey(sprint) )
            return false;
        List<User> allowedUsers = sprintWhiteList.get(sprint);
        return allowedUsers.stream().anyMatch(allowedUser -> allowedUser.equals(user));
    }

}
