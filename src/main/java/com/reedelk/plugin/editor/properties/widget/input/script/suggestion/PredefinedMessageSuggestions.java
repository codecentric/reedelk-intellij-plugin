package com.reedelk.plugin.editor.properties.widget.input.script.suggestion;

import java.util.Arrays;
import java.util.List;

public class PredefinedMessageSuggestions {

    public static final List<SuggestionToken> SUGGESTIONS = Arrays.asList(
            new SuggestionToken("payload", SuggestionType.VARIABLE),
            new SuggestionToken("message", SuggestionType.VARIABLE),
            new SuggestionToken("attributes", SuggestionType.VARIABLE),
            new SuggestionToken("message.content", SuggestionType.PROPERTY),
            new SuggestionToken("message.attributes", SuggestionType.PROPERTY),
            new SuggestionToken("message.content.data", SuggestionType.PROPERTY));
}
