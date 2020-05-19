package com.reedelk.plugin.service.module.impl.component.completion;

class SuggestionFactoryTest {

    // TODO: Fixme
    private TypeAndTries allTypesMap = new TypeAndTries();
    /**

    // Suggestions from global type
    @Test
    void shouldCorrectlyCreateSuggestionFromGlobalType() {
        // Given
        String type = "com.test.internal.type.FileType";
        TypeDescriptor fileType = createTypeDescriptor(type, emptyList(), emptyList());
        fileType.setGlobal(true);

        // When
        Suggestion suggestion = SuggestionFactory.create(fileType);

        // Then
        PluginAssertion.assertThat(suggestion)
                .hasType(GLOBAL)
                .hasLookupToken("FileType")
                .hasOffset(0)
                .hasReturnType(type)
                .hasInsertValue("FileType")
                .hasTailText(null)
                .hasReturnDisplayValue("FileType");
    }

    @Test
    void shouldCorrectlyCreateSuggestionFromGlobalTypeWhenDisplayNameIsPresent() {
        // Given
        String type = "com.test.internal.type.FileType";
        String displayName = "MyDisplayName";
        TypeDescriptor fileType = createTypeDescriptor(type, emptyList(), emptyList());
        fileType.setDisplayName(displayName);
        fileType.setGlobal(true);

        // When
        Suggestion suggestion = SuggestionFactory.create(fileType);

        // Then
        PluginAssertion.assertThat(suggestion)
                .hasType(GLOBAL)
                .hasLookupToken(displayName)
                .hasOffset(0)
                .hasReturnType(type)
                .hasInsertValue(displayName)
                .hasTailText(null)
                .hasReturnDisplayValue(displayName);
    }

    // Suggestions from function descriptor
    @Test
    void shouldCorrectlyCreateSuggestionFromTypeFunctionDescriptor() {
        // Given
        TypeFunctionDescriptor method =
                createFunctionDescriptor("method1", "method1(String key)", String.class.getName(), 1);

        // When
        Suggestion suggestion = SuggestionFactory.create(allTypesMap, method);

        // Then
        PluginAssertion.assertThat(suggestion)
                .hasType(FUNCTION)
                .hasLookupToken("method1")
                .hasOffset(1)
                .hasReturnType(String.class.getName())
                .hasInsertValue("method1()")
                .hasTailText("(String key)")
                .hasReturnDisplayValue("String");
    }

    @Test
    void shouldCorrectlyCreateSuggestionFromTypeFunctionDescriptorWhenReturnIsVoid() {
        // Given
        TypeFunctionDescriptor method =
                createFunctionDescriptor("method1", "method1(String key)", "void", 1);

        // When
        Suggestion suggestion = SuggestionFactory.create(allTypesMap, method);

        // Then
        PluginAssertion.assertThat(suggestion)
                .hasType(FUNCTION)
                .hasLookupToken("method1")
                .hasOffset(1)
                .hasReturnType("void")
                .hasInsertValue("method1()")
                .hasTailText("(String key)")
                .hasReturnDisplayValue("void");
    }

    @Test
    void shouldCorrectlyCreateSuggestionFromTypeFunctionDescriptorWhenReturnIsNull() {
        // Given
        TypeFunctionDescriptor method =
                createFunctionDescriptor("method1", "method1(String key)", null, 1);

        // When
        Suggestion suggestion = SuggestionFactory.create(allTypesMap, method);

        // Then
        PluginAssertion.assertThat(suggestion)
                .hasType(FUNCTION)
                .hasLookupToken("method1")
                .hasOffset(1)
                .hasReturnType("void")
                .hasInsertValue("method1()")
                .hasTailText("(String key)")
                .hasReturnDisplayValue("void");
    }

    @Test
    void shouldCorrectlyCreateSuggestionFromTypeFunctionDescriptorWhenReturnListWithItemType() {
        // Given
        Map<String, Trie> moduleTypes = new HashMap<>();
        moduleTypes.put("com.test.MyListItem", new TrieDefault(ArrayList.class.getName(), null, "com.test.MyItem", null, null));
        TypeAndTries allTypesMap = new TypeAndTries(moduleTypes);

        TypeFunctionDescriptor method =
                createFunctionDescriptor("method1", "method1(String key)", "com.test.MyListItem", 1);

        // When
        Suggestion suggestion = SuggestionFactory.create(allTypesMap, method);

        // Then
        PluginAssertion.assertThat(suggestion)
                .hasType(FUNCTION)
                .hasLookupToken("method1")
                .hasOffset(1)
                .hasReturnType("com.test.MyListItem")
                .hasInsertValue("method1()")
                .hasTailText("(String key)")
                .hasReturnDisplayValue("List<MyItem>");
    }

    @Test
    void shouldCorrectlyCreateSuggestionFromTypeFunctionDescriptorWhenReturnListWithItemTypeAndDisplayName() {
        // Given
        String displayName = "MyListItemDisplayName";
        Map<String, Trie> moduleTypes = new HashMap<>();
        moduleTypes.put("com.test.MyListItem", new TrieDefault(ArrayList.class.getName(), displayName, "com.test.MyItem", null, null));
        TypeAndTries allTypesMap = new TypeAndTries(moduleTypes);

        TypeFunctionDescriptor method =
                createFunctionDescriptor("method1", "method1(String key)", "com.test.MyListItem", 1);

        // When
        Suggestion suggestion = SuggestionFactory.create(allTypesMap, method);

        // Then
        PluginAssertion.assertThat(suggestion)
                .hasType(FUNCTION)
                .hasLookupToken("method1")
                .hasOffset(1)
                .hasReturnType("com.test.MyListItem")
                .hasInsertValue("method1()")
                .hasTailText("(String key)")
                .hasReturnDisplayValue("MyListItemDisplayName");
    }

    // Suggestions from property descriptor
    @Test
    void shouldCorrectlyCreateSuggestionFromTypePropertyDescriptor() {
        // Given
        TypePropertyDescriptor propertyDescriptor = createStringPropertyDescriptor("myProperty");

        // When
        Suggestion suggestion = SuggestionFactory.create(allTypesMap, propertyDescriptor);

        // Then
        PluginAssertion.assertThat(suggestion)
                .hasType(PROPERTY)
                .hasLookupToken("myProperty")
                .hasOffset(0)
                .hasReturnType(String.class.getName())
                .hasInsertValue("myProperty")
                .hasTailText(null)
                .hasReturnDisplayValue("String");
    }

    @Test
    void shouldCorrectlyCreateSuggestionFromTypePropertyDescriptorWhenReturnListWithItemType() {
        // Given
        String returnType = "com.test.MyListItem";
        Map<String, Trie> moduleTypes = new HashMap<>();
        moduleTypes.put(returnType, new TrieDefault(ArrayList.class.getName(), null, "com.test.MyItem", null, null));
        TypeAndTries allTypesMap = new TypeAndTries(moduleTypes);
        TypePropertyDescriptor propertyDescriptor = createPropertyDescriptor("myProperty", returnType);

        // When
        Suggestion suggestion = SuggestionFactory.create(allTypesMap, propertyDescriptor);

        // Then
        PluginAssertion.assertThat(suggestion)
                .hasType(PROPERTY)
                .hasLookupToken("myProperty")
                .hasOffset(0)
                .hasReturnType("com.test.MyListItem")
                .hasInsertValue("myProperty")
                .hasTailText(null)
                .hasReturnDisplayValue("List<MyItem>");
    }

    // Suggestions from script signature argument
    @Test
    void shouldCorrectlyCreateSuggestionFromScriptSignatureArgument() {
        // Given
        String argumentType = "com.test.MyListItem";
        Map<String, Trie> moduleTypes = new HashMap<>();
        moduleTypes.put(argumentType, new TrieDefault(ArrayList.class.getName(), null, "com.test.MyItem", null, null));
        TypeAndTries allTypesMap = new TypeAndTries(moduleTypes);
        ScriptSignatureArgument scriptSignatureArgument = createScriptSignatureArgument("argument1", argumentType);

        // When
        Suggestion suggestion = SuggestionFactory.create(allTypesMap, scriptSignatureArgument);

        // Then
        PluginAssertion.assertThat(suggestion)
                .hasType(PROPERTY)
                .hasLookupToken("argument1")
                .hasOffset(0)
                .hasReturnType(argumentType)
                .hasInsertValue("argument1")
                .hasTailText(null)
                .hasReturnDisplayValue("List<MyItem>");
    }

    @Test
    void shouldCorrectlyCreateSuggestionFromScriptSignatureArgumentWhenReturnListWithItemTypeAndDisplayName() {
        // Given
        String argumentType = "com.test.MyListItem";
        String displayName = "MyListItemDisplayName";
        Map<String, Trie> moduleTypes = new HashMap<>();
        moduleTypes.put(argumentType, new TrieDefault(ArrayList.class.getName(), displayName, "com.test.MyItem", null, null));
        TypeAndTries allTypesMap = new TypeAndTries(moduleTypes);

        ScriptSignatureArgument scriptSignatureArgument = createScriptSignatureArgument("argument1", argumentType);

        // When
        Suggestion suggestion = SuggestionFactory.create(allTypesMap, scriptSignatureArgument);

        // Then
        PluginAssertion.assertThat(suggestion)
                .hasType(PROPERTY)
                .hasLookupToken("argument1")
                .hasOffset(0)
                .hasReturnType(argumentType)
                .hasInsertValue("argument1")
                .hasTailText(null)
                .hasReturnDisplayValue(displayName);
    }*/
}
