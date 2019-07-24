package com.esb.plugin.editor.properties.widget.input.script.suggestion;

import java.util.Arrays;
import java.util.List;

public class PredefinedMessageSuggestions {

    public static final List<SuggestionToken> SUGGESTIONS = Arrays.asList(
            new SuggestionToken("message", SuggestionType.VARIABLE),
            new SuggestionToken("inboundProperties", SuggestionType.VARIABLE),
            new SuggestionToken("outboundProperties", SuggestionType.VARIABLE),
            new SuggestionToken("message.typedContent", SuggestionType.PROPERTY),
            new SuggestionToken("message.inboundProperties", SuggestionType.PROPERTY),
            new SuggestionToken("message.outboundProperties", SuggestionType.PROPERTY),
            new SuggestionToken("message.typedContent.content", SuggestionType.PROPERTY));
}
