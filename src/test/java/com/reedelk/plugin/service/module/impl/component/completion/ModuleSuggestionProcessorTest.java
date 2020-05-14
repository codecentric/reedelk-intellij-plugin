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
import java.util.List;
import java.util.Map;

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
    void shouldPopulateCorrectlyModuleTrieForType() {
        // Given
        String type = "com.test.internal.type.FileType";
        TypePropertyDescriptor idProperty = createStringProperty("id");
        TypePropertyDescriptor ageProperty = createIntProperty("age");
        TypeFunctionDescriptor method1 = createMethod("method1", "method1(String key)", String.class.getName(), 1);
        TypeFunctionDescriptor method2 = createMethod("method2", "method2()", int.class.getName());
        TypeDescriptor fileType = createType(type, asList(idProperty, ageProperty), asList(method1, method2));

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
                .contains("", "");
    }

    private void assertExistSuggestion(Collection<Suggestion> suggestions, Suggestion target) {

    }

    private TypePropertyDescriptor createStringProperty(String name) {
        TypePropertyDescriptor idProperty = new TypePropertyDescriptor();
        idProperty.setType(String.class.getName());
        idProperty.setName(name);
        return idProperty;
    }
    private TypePropertyDescriptor createIntProperty(String name) {
        TypePropertyDescriptor idProperty = new TypePropertyDescriptor();
        idProperty.setType(int.class.getName());
        idProperty.setName(name);
        return idProperty;
    }

    private TypeFunctionDescriptor createMethod(String name, String signature, String returnType, int cursorOffset) {
        TypeFunctionDescriptor method = new TypeFunctionDescriptor();
        method.setCursorOffset(cursorOffset);
        method.setReturnType(returnType);
        method.setSignature(signature);
        method.setName(name);
        return method;
    }

    private TypeFunctionDescriptor createMethod(String name, String signature, String returnType) {
        return createMethod(name, signature, returnType, 0);
    }

    private TypeDescriptor createType(String type, List<TypePropertyDescriptor> properties, List<TypeFunctionDescriptor> functions) {
        TypeDescriptor typeDescriptor = new TypeDescriptor();
        typeDescriptor.setProperties(properties);
        typeDescriptor.setFunctions(functions);
        typeDescriptor.setType(type);
        return typeDescriptor;
    }
}
