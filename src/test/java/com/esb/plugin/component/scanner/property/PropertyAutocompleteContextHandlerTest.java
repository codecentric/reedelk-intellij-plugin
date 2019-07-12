package com.esb.plugin.component.scanner.property;

import com.esb.plugin.component.domain.AutocompleteContext;
import com.esb.plugin.component.domain.ComponentPropertyDescriptor;
import com.esb.plugin.component.domain.TypeDescriptor;
import com.esb.plugin.component.scanner.AbstractScannerTest;
import io.github.classgraph.FieldInfo;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class PropertyAutocompleteContextHandlerTest extends AbstractScannerTest {

    private PropertyAutocompleteContextHandler handler = new PropertyAutocompleteContextHandler();

    @Test
    void shouldCorrectlyMapAutocompleteContextsAnnotations() {
        // Given
        FieldInfo scriptField = getTargetComponentClassInfo().getFieldInfo("script");

        // When
        ComponentPropertyDescriptor.Builder builder = ComponentPropertyDescriptor.builder();
        builder.propertyName("notRelevantPropertyName").type(new NotRelevantPropertyType());

        handler.handle(scriptField, builder, context());

        // Then
        ComponentPropertyDescriptor descriptor = builder.build();
        List<AutocompleteContext> contexts = descriptor.getAutocompleteContexts();
        assertThat(contexts).hasSize(3);

    }

    @Override
    protected Class targetComponentClazz() {
        return ComponentWithAutocompleteContexts.class;
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
