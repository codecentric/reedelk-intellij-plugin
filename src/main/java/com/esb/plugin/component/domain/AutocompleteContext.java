package com.esb.plugin.component.domain;

import com.esb.api.annotation.AutocompleteType;

public class AutocompleteContext {

    private final AutocompleteType autocompleteType;
    private final String propertyName;
    private final String contextName;
    private final String file;

    public AutocompleteContext(String contextName,
                               AutocompleteType autocompleteType,
                               String propertyName,
                               String file) {
        this.contextName = contextName;
        this.propertyName = propertyName;
        this.autocompleteType = autocompleteType;
        this.file = file;
    }

    public AutocompleteType getAutocompleteType() {
        return autocompleteType;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public String getContextName() {
        return contextName;
    }

    public String getFile() {
        return file;
    }
}
