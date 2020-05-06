package com.reedelk.plugin.service.module.impl.completion;

import com.reedelk.module.descriptor.model.type.TypeFunctionDescriptor;
import com.reedelk.plugin.service.module.impl.component.completion.TrieDefault;

import java.util.Map;

// TODO: Complete me.
class CompletionFinderTest {

    private TrieDefault global;
    private Map<String, TrieDefault> typeAndTriesMap;
/**
    @BeforeEach
    void setUp() {
        TypeFunctionDescriptor functionDescriptor = createFunction("tmpdir", String.class.getName());
        TypeDescriptor typeDescriptor = createType("Util", true, Arrays.asList(functionDescriptor), Collections.emptyList());

        global = new Trie();
        typeAndTriesMap = new HashMap<>();

        TrieUtils.populate(global, typeAndTriesMap, typeDescriptor);
    }

    @Test
    void should() {
        // Given
        String[] tokens = new String[]{"Uti"};

        // When
        List<Suggestion> suggestions =
                CompletionFinder.find(global, typeAndTriesMap, tokens);

        // Then
        assertThat(suggestions).isNotEmpty();
    }

    private TypeDescriptor createType(String displayName, boolean global, List<TypeFunctionDescriptor> functions, List<TypePropertyDescriptor> properties) {
        TypeDescriptor typeDescriptor = new TypeDescriptor();
        typeDescriptor.setGlobal(global);
        typeDescriptor.setDisplayName(displayName);
        typeDescriptor.setFunctions(functions);
        typeDescriptor.setProperties(properties);
        return typeDescriptor;
    }
*/
    private TypeFunctionDescriptor createFunction(String name, String type) {
        TypeFunctionDescriptor functionDescriptor = new TypeFunctionDescriptor();
        functionDescriptor.setName(name);
        functionDescriptor.setReturnType(type);
        functionDescriptor.setSignature(name + "()");
        return functionDescriptor;
    }
}
