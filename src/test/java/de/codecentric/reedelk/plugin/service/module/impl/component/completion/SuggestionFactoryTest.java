package de.codecentric.reedelk.plugin.service.module.impl.component.completion;

import de.codecentric.reedelk.plugin.assertion.PluginAssertion;
import de.codecentric.reedelk.module.descriptor.model.property.ScriptSignatureArgument;
import de.codecentric.reedelk.module.descriptor.model.type.TypeDescriptor;
import de.codecentric.reedelk.module.descriptor.model.type.TypeFunctionDescriptor;
import de.codecentric.reedelk.module.descriptor.model.type.TypePropertyDescriptor;
import org.junit.jupiter.api.Test;

import static java.util.Collections.emptyList;
import static de.codecentric.reedelk.plugin.service.module.impl.component.completion.TypeTestUtils.MyItemType;
import static de.codecentric.reedelk.plugin.service.module.impl.component.completion.Suggestion.Type.*;
import static de.codecentric.reedelk.plugin.service.module.impl.component.completion.SuggestionTestUtils.*;

class SuggestionFactoryTest extends AbstractCompletionTest {

    @Test
    void shouldCorrectlyCreateSuggestionFromGlobalType() {
        // Given
        String type = "com.test.internal.type.FileType";
        TypeDescriptor fileType = SuggestionTestUtils.createTypeDescriptor(type, emptyList(), emptyList());
        fileType.setGlobal(true);

        // When
        Suggestion suggestion = SuggestionFactory.create(typeAndTries, fileType);

        // Then
        PluginAssertion.assertThat(suggestion)
                .hasReturnDisplayValue("FileType")
                .hasLookupToken("FileType")
                .hasInsertValue("FileType")
                .hasReturnType(type)
                .hasTailText(null)
                .hasType(GLOBAL)
                .hasOffset(0);
    }

    @Test
    void shouldCorrectlyCreateSuggestionFromGlobalTypeWhenDisplayNameIsPresent() {
        // Given
        String type = "com.test.internal.type.FileType";
        String displayName = "MyDisplayName";
        TypeDescriptor fileType = SuggestionTestUtils.createTypeDescriptor(type, emptyList(), emptyList());
        fileType.setDisplayName(displayName);
        fileType.setGlobal(true);

        // When
        Suggestion suggestion = SuggestionFactory.create(typeAndTries, fileType);

        // Then
        PluginAssertion.assertThat(suggestion)
                .hasReturnDisplayValue(displayName)
                .hasLookupToken(displayName)
                .hasInsertValue(displayName)
                .hasReturnType(type)
                .hasTailText(null)
                .hasType(GLOBAL)
                .hasOffset(0);
    }

    // Suggestions from function descriptor
    @Test
    void shouldCorrectlyCreateSuggestionFromTypeFunctionDescriptor() {
        // Given
        TypeFunctionDescriptor method =
                SuggestionTestUtils.createFunctionDescriptor("method1", "method1(String key)", String.class.getName(), 1);

        // When
        Suggestion suggestion = SuggestionFactory.create(typeAndTries, method);

        // Then
        PluginAssertion.assertThat(suggestion)
                .hasReturnType(String.class.getName())
                .hasReturnDisplayValue("String")
                .hasTailText("(String key)")
                .hasInsertValue("method1()")
                .hasLookupToken("method1")
                .hasType(FUNCTION)
                .hasOffset(1);
    }

    @Test
    void shouldCorrectlyCreateSuggestionFromTypeFunctionDescriptorWhenReturnIsVoid() {
        // Given
        TypeFunctionDescriptor method =
                SuggestionTestUtils.createFunctionDescriptor("method1", "method1(String key)", "void", 1);

        // When
        Suggestion suggestion = SuggestionFactory.create(typeAndTries, method);

        // Then
        PluginAssertion.assertThat(suggestion)
                .hasReturnDisplayValue("void")
                .hasTailText("(String key)")
                .hasInsertValue("method1()")
                .hasLookupToken("method1")
                .hasReturnType("void")
                .hasType(FUNCTION)
                .hasOffset(1);
    }

    @Test
    void shouldCorrectlyCreateSuggestionFromTypeFunctionDescriptorWhenReturnIsNull() {
        // Given
        TypeFunctionDescriptor method =
                SuggestionTestUtils.createFunctionDescriptor("method1", "method1(String key)", Void.class.getName(), 1);

        // When
        Suggestion suggestion = SuggestionFactory.create(typeAndTries, method);

        // Then
        PluginAssertion.assertThat(suggestion)
                .hasReturnType(Void.class.getName())
                .hasReturnDisplayValue("void")
                .hasTailText("(String key)")
                .hasInsertValue("method1()")
                .hasLookupToken("method1")
                .hasType(FUNCTION)
                .hasOffset(1);
    }

    @Test
    void shouldCorrectlyCreateSuggestionFromTypeFunctionDescriptorWhenReturnListWithItemType() {
        // Given
        TypeFunctionDescriptor method =
                SuggestionTestUtils.createFunctionDescriptor("method1", "method1(String key)", TypeTestUtils.ListMyItemType.class.getName(), 1);

        // When
        Suggestion suggestion = SuggestionFactory.create(typeAndTries, method);

        // Then
        PluginAssertion.assertThat(suggestion)
                .hasReturnType(TypeTestUtils.ListMyItemType.class.getName())
                .hasReturnDisplayValue("List<TypeTestUtils$MyItemType>")
                .hasInsertValue("method1()")
                .hasTailText("(String key)")
                .hasLookupToken("method1")
                .hasType(FUNCTION)
                .hasOffset(1);
    }

    @Test
    void shouldCorrectlyCreateSuggestionFromTypeFunctionDescriptorWhenReturnListWithItemTypeAndDisplayName() {
        // Given
        TypeFunctionDescriptor method =
                SuggestionTestUtils.createFunctionDescriptor("method1", "method1(String key)", TypeTestUtils.MyItemType.class.getName(), 1);

        // When
        Suggestion suggestion = SuggestionFactory.create(typeAndTries, method);

        // Then
        PluginAssertion.assertThat(suggestion)
                .hasReturnDisplayValue("TypeTestUtils$MyItemType")
                .hasReturnType(TypeTestUtils.MyItemType.class.getName())
                .hasInsertValue("method1()")
                .hasTailText("(String key)")
                .hasLookupToken("method1")
                .hasType(FUNCTION)
                .hasOffset(1);
    }

    // Suggestions from property descriptor
    @Test
    void shouldCorrectlyCreateSuggestionFromTypePropertyDescriptor() {
        // Given
        TypePropertyDescriptor propertyDescriptor = SuggestionTestUtils.createStringPropertyDescriptor("myProperty");

        // When
        Suggestion suggestion = SuggestionFactory.create(typeAndTries, propertyDescriptor);

        // Then
        PluginAssertion.assertThat(suggestion)
                .hasReturnType(String.class.getName())
                .hasReturnDisplayValue("String")
                .hasLookupToken("myProperty")
                .hasInsertValue("myProperty")
                .hasType(PROPERTY)
                .hasTailText(null)
                .hasOffset(0);
    }

    @Test
    void shouldCorrectlyCreateSuggestionFromTypePropertyDescriptorWhenReturnListWithItemType() {
        // Given
        String returnType = TypeTestUtils.ListMyItemType.class.getName();
        TypePropertyDescriptor propertyDescriptor = SuggestionTestUtils.createPropertyDescriptor("myProperty", returnType);

        // When
        Suggestion suggestion = SuggestionFactory.create(typeAndTries, propertyDescriptor);

        // Then
        PluginAssertion.assertThat(suggestion)
                .hasReturnDisplayValue("List<TypeTestUtils$MyItemType>")
                .hasReturnType(TypeTestUtils.ListMyItemType.class.getName())
                .hasInsertValue("myProperty")
                .hasLookupToken("myProperty")
                .hasType(PROPERTY)
                .hasTailText(null)
                .hasOffset(0);
    }

    // Suggestions from script signature argument
    @Test
    void shouldCorrectlyCreateSuggestionFromScriptSignatureArgument() {
        // Given
        String argumentType = TypeTestUtils.ListMyItemType.class.getName();
        ScriptSignatureArgument scriptSignatureArgument = SuggestionTestUtils.createScriptSignatureArgument("argument1", argumentType);

        // When
        Suggestion suggestion = SuggestionFactory.create(typeAndTries, scriptSignatureArgument);

        // Then
        PluginAssertion.assertThat(suggestion)
                .hasReturnDisplayValue("List<TypeTestUtils$MyItemType>")
                .hasLookupToken("argument1")
                .hasInsertValue("argument1")
                .hasReturnType(argumentType)
                .hasType(PROPERTY)
                .hasTailText(null)
                .hasOffset(0);
    }

    @Test
    void shouldCorrectlyCreateSuggestionFromScriptSignatureArgumentWhenReturnListWithItemTypeAndDisplayName() {
        // Given
        String argumentType = TypeTestUtils.MapFirstType.class.getName();
        ScriptSignatureArgument scriptSignatureArgument = SuggestionTestUtils.createScriptSignatureArgument("argument1", argumentType);

        // When
        Suggestion suggestion = SuggestionFactory.create(typeAndTries, scriptSignatureArgument);

        // Then
        PluginAssertion.assertThat(suggestion)
                .hasReturnDisplayValue("MapFirstType")
                .hasLookupToken("argument1")
                .hasInsertValue("argument1")
                .hasReturnType(argumentType)
                .hasType(PROPERTY)
                .hasTailText(null)
                .hasOffset(0);
    }
}
