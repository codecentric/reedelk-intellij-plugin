package com.reedelk.plugin.editor.properties.widget.input.script.suggestion;

import java.util.Arrays;
import java.util.List;

class PredefinedMessageSuggestions {

    static final List<SuggestionToken> SUGGESTIONS = Arrays.asList(
            new SuggestionToken("message", SuggestionType.VARIABLE),
            new SuggestionToken("message.payload()", SuggestionType.FUNCTION),
            new SuggestionToken("attributes", SuggestionType.VARIABLE),
            new SuggestionToken("message.content", SuggestionType.PROPERTY),
            new SuggestionToken("message.attributes", SuggestionType.PROPERTY),
            new SuggestionToken("message.content.data()", SuggestionType.FUNCTION));
}
