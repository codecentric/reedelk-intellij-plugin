package com.reedelk.plugin.service.module.impl.component.completion;

import com.reedelk.module.descriptor.model.ModuleDescriptor;
import com.reedelk.module.descriptor.model.type.TypeDescriptor;
import com.reedelk.module.descriptor.model.type.TypeFunctionDescriptor;
import com.reedelk.module.descriptor.model.type.TypePropertyDescriptor;
import com.reedelk.plugin.assertion.PluginAssertion;
import com.reedelk.runtime.api.commons.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static com.reedelk.plugin.service.module.impl.component.completion.Suggestion.Type.FUNCTION;
import static com.reedelk.plugin.service.module.impl.component.completion.Suggestion.Type.PROPERTY;
import static com.reedelk.plugin.service.module.impl.component.completion.SuggestionTestUtils.*;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

class ModuleSuggestionProcessorTest {

    private Trie moduleGlobalTypes;
    private TypeAndTries allTypesMap;
    private Map<String, Trie> moduleTypes;
    private Map<String, Trie> moduleSignatureTypes;

    private ModuleSuggestionProcessor processor;

    @BeforeEach
    void setUp() {
        moduleTypes = new HashMap<>();
        moduleGlobalTypes = new TrieImpl();
        moduleSignatureTypes = new HashMap<>();
        allTypesMap = new TypeAndTries(moduleTypes);
        processor = new ModuleSuggestionProcessor(
                allTypesMap,
                moduleGlobalTypes,
                moduleTypes,
                moduleSignatureTypes);
    }

    @Test
    void shouldPopulateCorrectlyTrieForType() {
        // Given
        String type = "com.test.internal.type.FileType";
        TypePropertyDescriptor idProperty = createStringPropertyDescriptor("id");
        TypePropertyDescriptor ageProperty = createIntPropertyDescriptor("age");
        TypeFunctionDescriptor method1 = createFunctionDescriptor("method1", "method1(String key)", String.class.getName(), 1);
        TypeFunctionDescriptor method2 = createFunctionDescriptor("method2", "method2()", int.class.getName());
        TypeDescriptor fileType = createTypeDescriptor(type, asList(idProperty, ageProperty), asList(method1, method2));

        ModuleDescriptor descriptor = new ModuleDescriptor();
        descriptor.setName("module-xyz");
        descriptor.setTypes(singletonList(fileType));

        // When
        processor.populate(descriptor);

        // Then
        assertThat(moduleTypes).containsKey(type);
        Trie typeTrie = moduleTypes.get(type);

        Collection<Suggestion> suggestions = typeTrie.autocomplete(StringUtils.EMPTY, allTypesMap);
        assertThat(suggestions).hasSize(4);
        PluginAssertion.assertThat(suggestions)
                .contains(PROPERTY, "id", "id", String.class.getName(), String.class.getSimpleName())
                .contains(PROPERTY, "age", "age", int.class.getName(), int.class.getSimpleName())
                .contains(FUNCTION, "method1()", "method1", String.class.getName(), String.class.getSimpleName());
    }
}
