package com.esb.plugin.component.scanner;

import com.esb.api.annotation.AutocompleteType;

public class AutocompleteContext {

    private final String name;
    private final AutocompleteType autocompleteType;
    private final String jsonSchemaFile;
    private final String tokensFile;

    public AutocompleteContext(String name,
                               AutocompleteType autocompleteType,
                               String jsonSchemaFile,
                               String tokensFile) {
        this.name = name;
        this.autocompleteType = autocompleteType;
        this.jsonSchemaFile = jsonSchemaFile;
        this.tokensFile = tokensFile;
    }

    public String getName() {
        return name;
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
