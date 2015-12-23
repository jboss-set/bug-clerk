package org.jboss.jbossset.bugclerk.comments;

import static org.jboss.jbossset.bugclerk.utils.StringUtils.CLOSE_ID_SEPARATOR;
import static org.jboss.jbossset.bugclerk.utils.StringUtils.OPEN_ID_SEPARATOR;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jboss.set.aphrodite.domain.Comment;

public class CommentPatternMatcher {

    final Pattern pattern;

    static final String OPEN_LIST_OF_ITEMS = "(";
    static final String CLOSE_LIST_OF_ITEMS = "(";
    static final String ITEM_SEPARATOR = "|";

    public CommentPatternMatcher(String patternToFind) {
        pattern = Pattern.compile(escapeSpecialCharacter(patternToFind));
    }

    private String escapeSpecialCharacter(String content) {
        return content.replace(OPEN_ID_SEPARATOR, "\\" + OPEN_ID_SEPARATOR).replace(CLOSE_ID_SEPARATOR,
                "\\" + CLOSE_ID_SEPARATOR);
    }

    public boolean containsPattern(Collection<Comment> comments) {
        if (comments != null)
            for (Comment comment : comments)
                if (this.containsPattern(comment))
                    return true;
        return false;
    }

    private boolean containsPattern(Comment comment) {

        Matcher regexMatcher = pattern.matcher(comment.getBody());

        while (regexMatcher.find())
            if (regexMatcher.group().length() != 0 && !regexMatcher.group().isEmpty())
                return true;
        return false;
    }
}
