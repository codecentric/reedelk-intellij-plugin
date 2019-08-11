package com.reedelk.plugin.component.scanner.property;

import com.reedelk.runtime.api.annotation.AutocompleteContext;
import com.reedelk.runtime.api.annotation.AutocompleteType;

public class ComponentWithAutocompleteContexts {

    @AutocompleteContext(name = "inputContext", type = AutocompleteType.JSON_SCHEMA)
    @AutocompleteContext(name = "inputContextWithInlineSchema", type = AutocompleteType.JSON_SCHEMA, file = "metadata/person.schema.json")
    @AutocompleteContext(name = "anotherContext", type = AutocompleteType.TOKENS, file = "metadata/autocomplete.txt")
    private String script;

}
