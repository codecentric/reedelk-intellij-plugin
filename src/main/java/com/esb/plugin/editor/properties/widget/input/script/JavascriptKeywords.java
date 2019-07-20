package com.esb.plugin.editor.properties.widget.input.script;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class JavascriptKeywords {

    public static final List<SuggestionToken> KEYWORDS = Arrays.asList(
            new SuggestionToken("abstract", SuggestionType.LANGUAGE),
            new SuggestionToken("arguments", SuggestionType.LANGUAGE),
            new SuggestionToken("await", SuggestionType.LANGUAGE),
            new SuggestionToken("boolean", SuggestionType.LANGUAGE),
            new SuggestionToken("break", SuggestionType.LANGUAGE),
            new SuggestionToken("byte", SuggestionType.LANGUAGE),
            new SuggestionToken("case", SuggestionType.LANGUAGE),
            new SuggestionToken("catch", SuggestionType.LANGUAGE),
            new SuggestionToken("char", SuggestionType.LANGUAGE),
            new SuggestionToken("class", SuggestionType.LANGUAGE),
            new SuggestionToken("const", SuggestionType.LANGUAGE),
            new SuggestionToken("continue", SuggestionType.LANGUAGE),
            new SuggestionToken("debugger", SuggestionType.LANGUAGE),
            new SuggestionToken("default", SuggestionType.LANGUAGE),
            new SuggestionToken("delete", SuggestionType.LANGUAGE),
            new SuggestionToken("do", SuggestionType.LANGUAGE),
            new SuggestionToken("double", SuggestionType.LANGUAGE),
            new SuggestionToken("else", SuggestionType.LANGUAGE),
            new SuggestionToken("enum", SuggestionType.LANGUAGE),
            new SuggestionToken("eval", SuggestionType.LANGUAGE),
            new SuggestionToken("export", SuggestionType.LANGUAGE),
            new SuggestionToken("extends", SuggestionType.LANGUAGE),
            new SuggestionToken("false", SuggestionType.LANGUAGE),
            new SuggestionToken("final", SuggestionType.LANGUAGE),
            new SuggestionToken("finally", SuggestionType.LANGUAGE),
            new SuggestionToken("float", SuggestionType.LANGUAGE),
            new SuggestionToken("for", SuggestionType.LANGUAGE),
            new SuggestionToken("function", SuggestionType.LANGUAGE),
            new SuggestionToken("goto", SuggestionType.LANGUAGE),
            new SuggestionToken("if", SuggestionType.LANGUAGE),
            new SuggestionToken("implements", SuggestionType.LANGUAGE),
            new SuggestionToken("import", SuggestionType.LANGUAGE),
            new SuggestionToken("in", SuggestionType.LANGUAGE),
            new SuggestionToken("instanceof", SuggestionType.LANGUAGE),
            new SuggestionToken("int", SuggestionType.LANGUAGE),
            new SuggestionToken("interface", SuggestionType.LANGUAGE),
            new SuggestionToken("let", SuggestionType.LANGUAGE),
            new SuggestionToken("long", SuggestionType.LANGUAGE),
            new SuggestionToken("native", SuggestionType.LANGUAGE),
            new SuggestionToken("new", SuggestionType.LANGUAGE),
            new SuggestionToken("null", SuggestionType.LANGUAGE),
            new SuggestionToken("package", SuggestionType.LANGUAGE),
            new SuggestionToken("private", SuggestionType.LANGUAGE),
            new SuggestionToken("protected", SuggestionType.LANGUAGE),
            new SuggestionToken("public", SuggestionType.LANGUAGE),
            new SuggestionToken("return", SuggestionType.LANGUAGE),
            new SuggestionToken("short", SuggestionType.LANGUAGE),
            new SuggestionToken("static", SuggestionType.LANGUAGE),
            new SuggestionToken("super", SuggestionType.LANGUAGE),
            new SuggestionToken("switch", SuggestionType.LANGUAGE),
            new SuggestionToken("synchronized", SuggestionType.LANGUAGE),
            new SuggestionToken("this", SuggestionType.LANGUAGE),
            new SuggestionToken("throw", SuggestionType.LANGUAGE),
            new SuggestionToken("throws", SuggestionType.LANGUAGE),
            new SuggestionToken("transient", SuggestionType.LANGUAGE),
            new SuggestionToken("true", SuggestionType.LANGUAGE),
            new SuggestionToken("try", SuggestionType.LANGUAGE),
            new SuggestionToken("typeof", SuggestionType.LANGUAGE),
            new SuggestionToken("var", SuggestionType.LANGUAGE),
            new SuggestionToken("void", SuggestionType.LANGUAGE),
            new SuggestionToken("volatile", SuggestionType.LANGUAGE),
            new SuggestionToken("while", SuggestionType.LANGUAGE),
            new SuggestionToken("with", SuggestionType.LANGUAGE),
            new SuggestionToken("yield", SuggestionType.LANGUAGE));

    public static final String REGEX;

    static {
        List<String> suggestionValues = KEYWORDS.stream()
                .map(suggestionToken -> suggestionToken.value)
                .collect(Collectors.toList());
        String pipedKeywords = String.join("|", suggestionValues);
        REGEX = "(\\W)*(" + pipedKeywords + ")";
    }

}
