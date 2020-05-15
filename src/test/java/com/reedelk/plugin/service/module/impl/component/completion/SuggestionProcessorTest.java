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
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

class SuggestionProcessorTest {

    private Trie moduleGlobalTypes;
    private TypeAndTries allTypesMap;
    private Map<String, Trie> moduleTypes;
    private Map<String, Trie> moduleSignatureTypes;

    private SuggestionProcessor processor;

    @BeforeEach
    void setUp() {
        moduleTypes = new HashMap<>();
        moduleGlobalTypes = new TrieImpl();
        moduleSignatureTypes = new HashMap<>();
        allTypesMap = new TypeAndTries(moduleTypes);
        processor = new SuggestionProcessor(
                allTypesMap,
                moduleGlobalTypes,
                moduleTypes,
                moduleSignatureTypes);
    }

    @Test
    void shouldPopulateCorrectlyModuleTypes() {
        // Given
        String fileTypeQualifiedName = "com.test.internal.type.FileType";
        TypePropertyDescriptor idProperty = createStringPropertyDescriptor("id");
        TypePropertyDescriptor ageProperty = createIntPropertyDescriptor("age");
        TypeFunctionDescriptor method1 = createFunctionDescriptor("method1", "method1(String key)", String.class.getName(), 1);
        TypeFunctionDescriptor method2 = createFunctionDescriptor("method2", "method2()", int.class.getName());
        TypeDescriptor fileType = createTypeDescriptor(fileTypeQualifiedName, asList(idProperty, ageProperty), asList(method1, method2));

        String documentTypeQualifiedName = "com.test.internal.type.DocumentType";
        TypePropertyDescriptor nameProperty = createStringPropertyDescriptor("name");
        TypeFunctionDescriptor findMethod = createFunctionDescriptor("find", "find(String key)", int.class.getName(), 1);
        TypeDescriptor documentType = createTypeDescriptor(documentTypeQualifiedName, singletonList(nameProperty), singletonList(findMethod));

        ModuleDescriptor descriptor = new ModuleDescriptor();
        descriptor.setName("module-xyz");
        descriptor.setTypes(asList(fileType, documentType));

        // When
        processor.populate(descriptor);

        // Then (file type trie built correctly)
        assertThat(moduleTypes).containsKey(fileTypeQualifiedName);
        Trie fileTypeTrie = moduleTypes.get(fileTypeQualifiedName);

        Collection<Suggestion> suggestions = fileTypeTrie.autocomplete(StringUtils.EMPTY, allTypesMap);
        assertThat(suggestions).hasSize(4);
        PluginAssertion.assertThat(suggestions)
                .contains(PROPERTY, "id", "id", String.class.getName(), String.class.getSimpleName())
                .contains(PROPERTY, "age", "age", int.class.getName(), int.class.getSimpleName())
                .contains(FUNCTION, "method1()", "method1", String.class.getName(), String.class.getSimpleName())
                .contains(FUNCTION, "method2()", "method2", int.class.getName(), int.class.getSimpleName());

        // Then (document type trie built correctly)
        assertThat(moduleTypes).containsKey(documentTypeQualifiedName);
        Trie documentTypeTrie = moduleTypes.get(documentTypeQualifiedName);

        suggestions = documentTypeTrie.autocomplete(StringUtils.EMPTY, allTypesMap);
        assertThat(suggestions).hasSize(2);
        PluginAssertion.assertThat(suggestions)
                .contains(PROPERTY, "name", "name", String.class.getName(), String.class.getSimpleName())
                .contains(FUNCTION, "find()", "find", int.class.getName(), int.class.getSimpleName());
    }

    @Test
    void shouldPopulateCorrectlyModuleGlobalTypes() {
        // Given
        String type = "com.test.internal.type.FileType";
        TypeFunctionDescriptor method1 = createFunctionDescriptor("method1", "method1(String key)", String.class.getName(), 1);
        TypeDescriptor fileType = createTypeDescriptor(type, emptyList(), singletonList(method1));
        fileType.setGlobal(true);

        ModuleDescriptor descriptor = new ModuleDescriptor();
        descriptor.setName("module-xyz");
        descriptor.setTypes(singletonList(fileType));

        // When
        processor.populate(descriptor);

        // Then
        Collection<Suggestion> suggestions = moduleGlobalTypes.autocomplete(StringUtils.EMPTY, allTypesMap);
        assertThat(suggestions).hasSize(1);
    }
}
