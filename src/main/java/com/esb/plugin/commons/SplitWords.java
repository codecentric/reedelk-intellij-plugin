package com.esb.plugin.commons;

import com.google.common.base.Joiner;

import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SplitWords {

    private static final Pattern PUNCTSPACE = Pattern.compile("[ \\p{Punct}]+");
    private static final Pattern TRANSITION = Pattern.compile("(?<=[^\\p{Lu}])(?=[\\p{Lu}])|(?=[\\p{Lu}][^\\p{Lu}])");

    public static String from(String text) {
        return Joiner.on(" ")
                .join(Arrays.stream(PUNCTSPACE.split(text))
                        .filter(word -> !word.isEmpty())
                        .flatMap(word -> Arrays.asList(TRANSITION.split(word)).stream())
                        .collect(Collectors.toList()));
    }
}
