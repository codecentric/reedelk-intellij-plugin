package com.reedelk.plugin.editor.properties.widget.input.script.suggestion;

import java.util.Arrays;
import java.util.List;

import static com.reedelk.plugin.editor.properties.widget.input.script.suggestion.SuggestionType.FUNCTION;
import static com.reedelk.plugin.editor.properties.widget.input.script.suggestion.SuggestionType.VARIABLE;

class PredefinedMessageSuggestions {

    static final List<SuggestionToken> SUGGESTIONS = Arrays.asList(
            new SuggestionToken("context", VARIABLE),

            new SuggestionToken("message", VARIABLE),
            new SuggestionToken("message.toString()", FUNCTION),
            new SuggestionToken("message.payload()", FUNCTION),

            new SuggestionToken("message.attributes", VARIABLE),
            new SuggestionToken("message.attributes.get()", FUNCTION),
            new SuggestionToken("message.attributes.toString()", FUNCTION),

            new SuggestionToken("message.content", VARIABLE),
            new SuggestionToken("message.content.toString()", FUNCTION),
            new SuggestionToken("message.content.data()", FUNCTION),
            new SuggestionToken("message.content.type()", FUNCTION),
            new SuggestionToken("message.content.stream()", FUNCTION),
            new SuggestionToken("message.content.isStream()", FUNCTION),
            new SuggestionToken("message.content.isConsumed()", FUNCTION),

            new SuggestionToken("message.content.mimeType", VARIABLE),
            new SuggestionToken("message.content.mimeType.toString()", FUNCTION),
            new SuggestionToken("message.content.mimeType.params", VARIABLE),
            new SuggestionToken("message.content.mimeType.subType", VARIABLE),
            new SuggestionToken("message.content.mimeType.charset", VARIABLE),
            new SuggestionToken("message.content.mimeType.primaryType", VARIABLE)
    );
}
