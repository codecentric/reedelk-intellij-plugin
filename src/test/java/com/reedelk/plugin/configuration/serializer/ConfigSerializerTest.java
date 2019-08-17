package com.reedelk.plugin.configuration.serializer;

import com.reedelk.plugin.configuration.ConfigSerializer;
import com.reedelk.plugin.fixture.ComponentNode1;
import com.reedelk.plugin.fixture.ComponentNode2;
import com.reedelk.plugin.service.module.impl.ConfigMetadata;
import com.reedelk.runtime.commons.JsonParser;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import static com.reedelk.plugin.component.domain.TypeObjectDescriptor.TypeObject;
import static com.reedelk.plugin.fixture.Json.Configuration;

class ConfigSerializerTest {

    @Test
    void shouldSerializeConfigCorrectly() {
        // Given
        TypeObject componentNode1TypeObject = new TypeObject(ComponentNode1.class.getName());
        componentNode1TypeObject.set(JsonParser.Config.id(), "38add40d-6a29-4e9e-9620-2bf165276204");
        componentNode1TypeObject.set(JsonParser.Config.title(), "Sample title");
        componentNode1TypeObject.set("property1", "my property 1 value");
        componentNode1TypeObject.set("property2", 473);
        componentNode1TypeObject.set("property3", true);
        ConfigMetadata metadata = new ConfigMetadata(componentNode1TypeObject);

        // When
        String actualJson = ConfigSerializer.serialize(metadata);

        // Then
        String expectedJson = Configuration.Sample.json();
        JSONAssert.assertEquals(expectedJson, actualJson, true);
    }

    @Test
    void shouldSerializeConfigWithNestedConfigObjectCorrectly() {
        // Given
        TypeObject componentNode2TypeObject = new TypeObject(ComponentNode2.class.getName());
        componentNode2TypeObject.set("property5", "my property 5 value");
        componentNode2TypeObject.set("property6", true);

        TypeObject componentNode1TypeObject = new TypeObject(ComponentNode1.class.getName());
        componentNode1TypeObject.set(JsonParser.Config.id(), "38add40d-6a29-4e9e-9620-2bf165276204");
        componentNode1TypeObject.set(JsonParser.Config.title(), "Sample title");
        componentNode1TypeObject.set("property1", "my property 1 value");
        componentNode1TypeObject.set("property2", 473);
        componentNode1TypeObject.set("property3", true);
        componentNode1TypeObject.set("property4", componentNode2TypeObject);
        ConfigMetadata metadata = new ConfigMetadata(componentNode1TypeObject);

        // When
        String actualJson = ConfigSerializer.serialize(metadata);

        // Then
        String expectedJson = Configuration.NestedConfig.json();
        JSONAssert.assertEquals(expectedJson, actualJson, true);
    }
}