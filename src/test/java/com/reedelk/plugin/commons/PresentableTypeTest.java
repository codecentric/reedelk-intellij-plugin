package com.reedelk.plugin.commons;

import com.reedelk.plugin.service.module.impl.component.completion.commons.PresentableType;
import com.reedelk.runtime.api.commons.StringUtils;
import com.reedelk.runtime.api.message.Message;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PresentableTypeTest {

    @Test
    void shouldConvertNullInputToEmptyString() {
        // Given
        String input = null;

        // When
        String actual = PresentableType.from(input);

        // Then
        assertThat(actual).isEqualTo(StringUtils.EMPTY);
    }

    @Test
    void shouldConvertFullyQualifiedNameToSimpleName() {
        // Given
        String input = Message.class.getName();

        // When
        String actual = PresentableType.from(input);

        // Then
        assertThat(actual).isEqualTo("Message");
    }

    @Test
    void shouldKeepSimpleNameAsIs() {
        // Given
        String input = "Message";

        // When
        String actual = PresentableType.from(input);

        // Then
        assertThat(actual).isEqualTo("Message");
    }
}
