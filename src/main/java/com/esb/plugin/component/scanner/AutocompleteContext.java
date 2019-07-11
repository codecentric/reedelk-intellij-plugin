package com.esb.plugin.component.scanner;

import com.esb.api.annotation.AutocompleteType;

public class AutocompleteContext {

    private final AutocompleteType autocompleteType;

    private final String contextName;
    private final String tokensFile;
    private final String jsonSchemaFile;

    public AutocompleteContext(String contextName,
                               AutocompleteType autocompleteType,
                               String jsonSchemaFile,
                               String tokensFile) {
        this.contextName = contextName;
        this.autocompleteType = autocompleteType;
        this.jsonSchemaFile = jsonSchemaFile;
        this.tokensFile = tokensFile;
    }

    public String getContextName() {
        return contextName;
    }

    public AutocompleteType getAutocompleteType() {
        return autocompleteType;
    }

    public String getJsonSchemaFile() {
        return jsonSchemaFile;
    }

    public String getTokensFile() {
        return tokensFile;
    }
}
