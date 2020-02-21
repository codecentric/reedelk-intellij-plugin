package com.reedelk.plugin.commons;

import com.reedelk.module.descriptor.model.PropertyDescriptor;
import com.reedelk.module.descriptor.model.TypePrimitiveDescriptor;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class TooltipContentTest {

    @Test
    void shouldReturnEmptyWhenDescriptionExampleAndDefaultValueAreNull() {
        // Given
        PropertyDescriptor descriptor = PropertyDescriptor.builder()
                .name("myProperty")
                .type(new TypePrimitiveDescriptor())
                .build();
        TooltipContent tooltipContent = TooltipContent.from(descriptor);

        // When
        Optional<String> content = tooltipContent.build();

        // Then
        assertThat(content).isEmpty();
    }

    @Test
    void shouldReturnTooltipTextCorrectly() {
        // Given
        PropertyDescriptor descriptor = PropertyDescriptor.builder()
                .name("myProperty")
                .description("my description")
                .example("<code>message.payload()</code>")
                .defaultValue("'hello world'")
                .type(new TypePrimitiveDescriptor())
                .build();
        TooltipContent tooltipContent = TooltipContent.from(descriptor);

        // When
        Optional<String> content = tooltipContent.build();

        // Then
        assertThat(content).contains("<p>my description<p><br><b>" +
                "Example: </b><code>message.payload()</code><br><b>" +
                "Default: </b>'hello world'");
    }
}
