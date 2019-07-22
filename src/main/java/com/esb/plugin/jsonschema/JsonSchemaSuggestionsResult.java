package com.esb.plugin.jsonschema;

import java.util.Collections;
import java.util.List;

public class JsonSchemaSuggestionsResult {

    private List<String> tokens;

    JsonSchemaSuggestionsResult(List<String> tokens) {
        this.tokens = tokens;
    }

    public List<String> getTokens() {
        return Collections.unmodifiableList(tokens);
    }
}
