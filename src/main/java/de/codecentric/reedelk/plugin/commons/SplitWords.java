package de.codecentric.reedelk.plugin.commons;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SplitWords {

    private static final Pattern PUNCTSPACE = Pattern.compile("[ \\p{Punct}]+");
    private static final Pattern TRANSITION = Pattern.compile("(?<=[^\\p{Lu}])(?=[\\p{Lu}])|(?=[\\p{Lu}][^\\p{Lu}])");

    private SplitWords() {
    }

    public static String from(String text) {

        List<String> words = Arrays.stream(PUNCTSPACE.split(text))
                .filter(word -> !word.isEmpty())
                .flatMap(word -> Arrays.stream(TRANSITION.split(word)))
                .collect(Collectors.toList());

        return String.join(" ", words);
    }
}
