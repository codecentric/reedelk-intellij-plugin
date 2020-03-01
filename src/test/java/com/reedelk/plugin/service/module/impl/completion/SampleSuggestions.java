package com.reedelk.plugin.service.module.impl.completion;

import com.reedelk.module.descriptor.model.AutocompleteItemDescriptor;
import com.reedelk.runtime.api.autocomplete.AutocompleteItemType;

public class SampleSuggestions {

    private static final AutocompleteItemDescriptor UTIL_AUTOCOMPLETE_TYPE = AutocompleteItemDescriptor.create()
            .global(true)
            .type("Util")
            .token("Util")
            .returnType("Util")
            .replaceValue("Util")
            .itemType(AutocompleteItemType.VARIABLE)
            .build();
    public static final Suggestion UTIL = Suggestion.create(UTIL_AUTOCOMPLETE_TYPE);

    private static final AutocompleteItemDescriptor CONFIG_AUTOCOMPLETE_TYPE = AutocompleteItemDescriptor.create()
            .global(true)
            .type("Config")
            .token("Config")
            .returnType("Config")
            .replaceValue("Config")
            .itemType(AutocompleteItemType.VARIABLE)
            .build();

    public static final Suggestion CONFIG = Suggestion.create(CONFIG_AUTOCOMPLETE_TYPE);

}
