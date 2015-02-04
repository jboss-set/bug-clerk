package org.jboss.jbossset.bugclerk.bugzilla;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jboss.pull.shared.connectors.bugzilla.Comment;

public class CommentPatternMatcher {

    final Pattern pattern;

    final static String OPEN_LIST_OF_ITEMS = "(";
    final static String CLOSE_LIST_OF_ITEMS = "(";
    final static String ITEM_SEPARATOR = "|";

    public CommentPatternMatcher(String string) {
        pattern = Pattern.compile(string);
    }

    private String buildRegexPattern(Collection<String> items) {
        StringBuffer pattern = new StringBuffer(OPEN_LIST_OF_ITEMS);
        for (String string : items)
            pattern.append(string).append(ITEM_SEPARATOR);
        return pattern.replace(0, pattern.length() - 1, CLOSE_LIST_OF_ITEMS).toString();
    }

    public boolean containsPattern(Collection<Comment> comments) {
        for (Comment comment : comments)
            if (this.containsPattern(comment))
                return true;
        return false;
    }

    private boolean containsPattern(Comment comment) {

        Matcher regexMatcher = pattern.matcher(comment.getText());

        while (regexMatcher.find())
            if (regexMatcher.group().length() != 0 && !regexMatcher.group().isEmpty())
                return true;
        return false;
    }
}
