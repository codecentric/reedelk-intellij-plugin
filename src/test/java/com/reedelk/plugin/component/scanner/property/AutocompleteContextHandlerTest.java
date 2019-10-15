package com.reedelk.plugin.component.scanner.property;

import com.reedelk.plugin.component.domain.AutocompleteContext;
import com.reedelk.plugin.component.domain.ComponentPropertyDescriptor;
import com.reedelk.plugin.component.domain.TypeDescriptor;
import com.reedelk.plugin.component.scanner.AbstractScannerTest;
import com.reedelk.plugin.component.scanner.ComponentAnalyzerContext;
import com.reedelk.runtime.api.annotation.AutocompleteType;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.FieldInfo;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

class AutocompleteContextHandlerTest extends AbstractScannerTest {

    private AutocompleteContextHandler handler = new AutocompleteContextHandler();

    private static ClassInfo componentClassInfo;
    private static ComponentAnalyzerContext context;

    @BeforeAll
    static void beforeAll() {
        ScanContext scanContext = scan(ComponentWithAutocompleteContexts.class);
        context = scanContext.context;
        componentClassInfo = scanContext.targetComponentClassInfo;
    }

    @Test
    void shouldCorrectlyMapAutocompleteContextsAnnotations() {
        // Given
        FieldInfo scriptField = componentClassInfo.getFieldInfo("script");

        // When
        ComponentPropertyDescriptor.Builder builder = ComponentPropertyDescriptor.builder();
        builder.propertyName("notRelevantPropertyName").type(new NotRelevantPropertyType());

        handler.handle(scriptField, builder, context);

        // Then
        ComponentPropertyDescriptor descriptor = builder.build();
        List<AutocompleteContext> contexts = descriptor.getAutocompleteContexts();
        assertThat(contexts).hasSize(3);

        assertExistsAutocompleteContextMatching(contexts, "inputContext", "script", AutocompleteType.JSON_SCHEMA, null);
        assertExistsAutocompleteContextMatching(contexts, "inputContextWithInlineSchema", "script", AutocompleteType.JSON_SCHEMA, "metadata/person.schema.json");
        assertExistsAutocompleteContextMatching(contexts, "anotherContext", "script", AutocompleteType.TOKENS, "metadata/autocomplete.txt");
    }

    private void assertExistsAutocompleteContextMatching(List<AutocompleteContext> collection,
                                                         String expectedContextName,
                                                         String expectedPropertyName,
                                                         AutocompleteType expectedAutocompleteType,
                                                         String expectedFileName) {
        for (AutocompleteContext context : collection) {
            String actualFileName = context.getFile();
            String actualContextName = context.getContextName();
            String actualPropertyName = context.getPropertyName();
            AutocompleteType actualAutocompleteType = context.getAutocompleteType();
            if ((expectedFileName == null && actualFileName == null || expectedFileName.equals(actualFileName)) &&
                    expectedContextName.equals(actualContextName) &&
                    expectedPropertyName.equals(actualPropertyName) &&
                    expectedAutocompleteType.equals(actualAutocompleteType)) return;
        }
        fail("Could not find Autocomplete Context definition matching context name=%s, property name=%s, type=%s, file name=%s");
    }

    class NotRelevantPropertyType implements TypeDescriptor {

        @Override
        public Class<?> type() {
            return int.class;
        }

        @Override
        public Object defaultValue() {
            return 12;
        }
    }
}