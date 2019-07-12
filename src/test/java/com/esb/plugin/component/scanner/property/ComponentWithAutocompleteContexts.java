package com.esb.plugin.component.scanner.property;

import com.esb.api.annotation.AutocompleteContext;
import com.esb.api.annotation.AutocompleteType;

public class ComponentWithAutocompleteContexts {

    @AutocompleteContext(name = "inputContext", type = AutocompleteType.JSON_SCHEMA)
    @AutocompleteContext(name = "inputContextWithInlineSchema", type = AutocompleteType.JSON_SCHEMA, file = "metadata/person.schema.json")
    @AutocompleteContext(name = "anotherContext", type = AutocompleteType.TOKENS, file = "metadata/autocomplete.txt")
    private String script;

}
