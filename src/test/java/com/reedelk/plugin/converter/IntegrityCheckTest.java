package com.reedelk.plugin.converter;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class IntegrityCheckTest {

    /**
     * Just an integrity test to verify that the converters for ConfigPropertyAwareConverters and
     * DefaultConverters are kept in sync. They *MUST* contain exactly the same converters for each type.
     * @see com.reedelk.plugin.converter.ConfigPropertyAwareConverters
     * @see com.reedelk.plugin.converter.DefaultConverters
     */
    @Test
    void shouldContainSameNumberOfConvertersCheck() {
        // Given
        int configPropertyAwareConvertersSize = ConfigPropertyAwareConverters.getConvertersCount();
        int defaultsConvertersSize = DefaultConverters.getConvertersCount();

        // Then
        assertThat(configPropertyAwareConvertersSize).isEqualTo(defaultsConvertersSize);
    }
}