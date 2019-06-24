package com.esb.plugin.configuration.serializer;

import com.esb.internal.commons.JsonParser;
import com.esb.plugin.component.domain.TypeObjectDescriptor;
import com.esb.plugin.fixture.ComponentNode1;
import com.esb.plugin.service.module.impl.ConfigMetadata;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import static com.esb.plugin.fixture.Json.Configuration;

class ConfigSerializerTest {

    @Test
    void shouldSerializeConfigCorrectly() {
        // Given
        TypeObjectDescriptor.TypeObject typeObject = new TypeObjectDescriptor.TypeObject(ComponentNode1.class.getName());
        typeObject.set(JsonParser.Config.id(), "38add40d-6a29-4e9e-9620-2bf165276204");
        typeObject.set(JsonParser.Config.title(), "Sample title");
        typeObject.set("property1", "my property 1 value");
        typeObject.set("property2", 473);
        typeObject.set("property3", true);
        ConfigMetadata metadata = new ConfigMetadata(typeObject);

        // When
        String actualJson = ConfigSerializer.serialize(metadata);

        // Then
        String expectedJson = Configuration.Sample.json();
        JSONAssert.assertEquals(expectedJson, actualJson, true);
    }
}