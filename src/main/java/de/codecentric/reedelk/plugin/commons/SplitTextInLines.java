package de.codecentric.reedelk.plugin.commons;

import de.codecentric.reedelk.runtime.api.commons.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SplitTextInLines {

    private SplitTextInLines() {
    }

    public static List<String> from(String text, Pattern regex) {
        if (StringUtils.isBlank(text)) {
            return Collections.emptyList();
        }

        List<String> matchList = new ArrayList<>();
        Matcher regexMatcher = regex.matcher(text);
        while (regexMatcher.find()) {
            matchList.add(regexMatcher.group());
        }
        return matchList;
    }
}
