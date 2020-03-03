package com.reedelk.plugin.service.module.impl.completion;

import com.reedelk.module.descriptor.model.AutocompleteItemDescriptor;
import com.reedelk.module.descriptor.model.AutocompleteTypeDescriptor;
import com.reedelk.module.descriptor.model.AutocompleteVariableDescriptor;
import com.reedelk.runtime.api.autocomplete.AutocompleteItemType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SuggestionTest {

    @Test
    void shouldCorrectlyComputeSuggestionForAutocompleteType() {
        // Given
        AutocompleteTypeDescriptor descriptor =
                AutocompleteTypeDescriptor.create()
                        .type("MyType")
                        .global(true)
                        .description("My description")
                        .build();
        // When
        Suggestion suggestion = Suggestion.create(descriptor);

        // Then
        assertThat(suggestion.getSuggestion()).isEqualTo("MyType");
    }

    @Test
    void shouldCorrectlyComputeSuggestionForAutocompleteItemWithTypeFunction() {
        // Given
        AutocompleteItemDescriptor descriptor =
                AutocompleteItemDescriptor.create()
                        .type("MyType")
                        .itemType(AutocompleteItemType.FUNCTION)
                        .token("method1")
                        .signature("method1(key: String)")
                        .build();

        // When
        Suggestion suggestion = Suggestion.create(descriptor);

        // Then
        assertThat(suggestion.getSuggestion()).isEqualTo("method1()");
    }

    @Test
    void shouldCorrectlyComputeSuggestionForAutocompleteItemWithTypeVariable() {
        // Given
        AutocompleteItemDescriptor descriptor =
                AutocompleteItemDescriptor.create()
                        .type("MyType")
                        .itemType(AutocompleteItemType.VARIABLE)
                        .token("correlationId")
                        .build();

        // When
        Suggestion suggestion = Suggestion.create(descriptor);

        // Then
        assertThat(suggestion.getSuggestion()).isEqualTo("correlationId");
    }

    @Test
    void shouldCorrectlyComputeSuggestionForAutocompleteVariable() {
        // Given
        AutocompleteVariableDescriptor descriptor = new AutocompleteVariableDescriptor();
        descriptor.setName("myVar");
        descriptor.setType("Message");

        // When
        Suggestion suggestion = Suggestion.create(descriptor);

        // Then
        assertThat(suggestion.getSuggestion()).isEqualTo("myVar");
    }
}
