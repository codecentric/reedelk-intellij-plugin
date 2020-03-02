package com.reedelk.plugin.service.module.impl.completion;

import com.reedelk.module.descriptor.model.AutocompleteItemDescriptor;
import com.reedelk.module.descriptor.model.AutocompleteTypeDescriptor;
import com.reedelk.runtime.api.autocomplete.AutocompleteItemType;

public class SampleSuggestions {

    private static final AutocompleteTypeDescriptor UTIL_AUTOCOMPLETE_TYPE = AutocompleteTypeDescriptor.create()
            .type("Util")
            .global(true)
            .description("Test description")
            .build();
    public static final Suggestion UTIL = Suggestion.create(UTIL_AUTOCOMPLETE_TYPE);

    private static final AutocompleteTypeDescriptor CONFIG_AUTOCOMPLETE_TYPE = AutocompleteTypeDescriptor.create()
            .type("Config")
            .global(true)
            .description("Test description")
            .build();
    public static final Suggestion CONFIG = Suggestion.create(CONFIG_AUTOCOMPLETE_TYPE);

    private static final AutocompleteItemDescriptor TMP_DIR_ITEM = AutocompleteItemDescriptor.create()
            .type("Util")
            .token("tmpdir")
            .returnType("String")
            .signature("tmpdir()")
            .itemType(AutocompleteItemType.FUNCTION)
            .build();
    public static final Suggestion TMP_DIR = Suggestion.create(TMP_DIR_ITEM);

    private static final AutocompleteItemDescriptor UUID_ITEM = AutocompleteItemDescriptor.create()
            .type("Util")
            .token("uuid")
            .returnType("String")
            .signature("uuid()")
            .itemType(AutocompleteItemType.FUNCTION)
            .build();
    public static final Suggestion UUID = Suggestion.create(UUID_ITEM);

}
