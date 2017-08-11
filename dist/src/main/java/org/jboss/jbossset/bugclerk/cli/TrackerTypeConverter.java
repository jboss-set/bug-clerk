package org.jboss.jbossset.bugclerk.cli;

import java.util.Locale;

import org.jboss.set.aphrodite.config.TrackerType;

import com.beust.jcommander.IStringConverter;

public class TrackerTypeConverter implements IStringConverter<TrackerType> {

    @Override
    public TrackerType convert(String value) {
        switch (value.toLowerCase(Locale.getDefault())) {
            case "jira":
                return TrackerType.JIRA;
            case "bugzilla":
                return TrackerType.BUGZILLA;
            default:
                throw new IllegalArgumentException("Unsupported tracker type:" + value + ", supported type are "
                        + TrackerType.values());
        }
    }
}
