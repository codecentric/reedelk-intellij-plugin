package com.esb.plugin.component.scanner;

import com.esb.api.annotation.AutocompleteType;

public class AutocompleteContext {

    private final String tokensFile;
    private final String contextName;
    private final String propertyName;
    private final String jsonSchemaFile;
    private final AutocompleteType autocompleteType;

    public AutocompleteContext(String contextName,
                               AutocompleteType autocompleteType,
                               String jsonSchemaFile,
                               String tokensFile,
                               String propertyName) {
        this.tokensFile = tokensFile;
        this.contextName = contextName;
        this.propertyName = propertyName;
        this.jsonSchemaFile = jsonSchemaFile;
        this.autocompleteType = autocompleteType;
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

    public String getPropertyName() {
        return propertyName;
    }
}
