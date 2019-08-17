package com.reedelk.plugin.configuration;

import com.reedelk.plugin.component.domain.ComponentDataHolder;
import com.reedelk.plugin.component.domain.ComponentPropertyDescriptor;
import com.reedelk.plugin.fixture.Json;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static com.reedelk.plugin.component.type.generic.SamplePropertyDescriptors.property4;

class ConfigDeserializerTest {

    @Test
    void shouldDoSomething() {
        // Given
        String json = Json.Configuration.Sample.json();
        ComponentPropertyDescriptor descriptor = property4;

        // When
        Optional<ComponentDataHolder> deserialize = ConfigDeserializer.deserialize(json, descriptor);

        // Then
        Assertions.assertThat(deserialize).isPresent();
    }
}