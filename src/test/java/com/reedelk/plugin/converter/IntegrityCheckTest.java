package com.reedelk.plugin.converter;

import com.reedelk.runtime.api.commons.PlatformTypes;
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
        int configPropertyAwareConvertersSize = ConfigPropertyAwareConverters.size();
        int defaultsConvertersSize = DefaultConverters.size();

        // Then
        assertThat(configPropertyAwareConvertersSize).isEqualTo(defaultsConvertersSize);
    }

    @Test
    void shouldHaveADefaultConverterForEachPlatformType() {
        // Given
        int platformTypesSize = PlatformTypes.size();
        int defaultsConvertersSize = DefaultConverters.size();
        assertThat(platformTypesSize)
                .withFailMessage("The number of default converters do not match the supported platform types!")
                .isEqualTo(defaultsConvertersSize);

        // Expect
        DefaultConverters.supportedConverters().forEach(supportedConverterClazz -> {
            String typeFullyQualifiedName = supportedConverterClazz.getName();
            boolean supported = PlatformTypes.isSupported(typeFullyQualifiedName);
            assertThat(supported)
                    .withFailMessage("The default converter type=[" + typeFullyQualifiedName + "] is " +
                            "not a supported platform type! Add the type to the supported platform types or " +
                            "remove the converter.")
                    .isTrue();
        });
    }

    @Test
    void shouldHaveAConfigPropertyAwareConverterForEachPlatformType() {
        // Given
        int platformTypesSize = PlatformTypes.size();
        int configPropertyAwareConvertersSize = ConfigPropertyAwareConverters.size();
        assertThat(platformTypesSize)
                .withFailMessage("The number of config property aware converters do not match the supported platform types!")
                .isEqualTo(configPropertyAwareConvertersSize);

        // Expect
        ConfigPropertyAwareConverters.supportedConverters().forEach(supportedConverterClazz -> {
            String typeFullyQualifiedName = supportedConverterClazz.getName();
            boolean supported = PlatformTypes.isSupported(typeFullyQualifiedName);
            assertThat(supported)
                    .withFailMessage("The config property aware converter type=[" + typeFullyQualifiedName + "] is " +
                            "not a supported platform type! Add the type to the supported platform types or " +
                            "remove the converter.")
                    .isTrue();
        });
    }
}