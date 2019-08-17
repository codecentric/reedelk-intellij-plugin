package com.reedelk.plugin.configuration;

import com.reedelk.plugin.fixture.ComponentNode1;
import com.reedelk.plugin.fixture.ComponentNode2;
import com.reedelk.plugin.service.module.impl.ConfigMetadata;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import static com.reedelk.plugin.component.domain.TypeObjectDescriptor.TypeObject;
import static com.reedelk.plugin.fixture.Json.Configuration;
import static com.reedelk.runtime.commons.JsonParser.Config;

class ConfigSerializerTest {

    @Test
    void shouldSerializeConfigCorrectly() {
        // Given
        TypeObject type1 = new TypeObject(ComponentNode1.class.getName());
        type1.set(Config.id(), "38add40d-6a29-4e9e-9620-2bf165276204");
        type1.set(Config.title(), "Sample title");
        type1.set("property1", "my property 1 value");
        type1.set("property2", 473);
        type1.set("property3", true);
        ConfigMetadata metadata = new ConfigMetadata(type1);

        // When
        String actualJson = ConfigSerializer.serialize(metadata);

        // Then
        String expectedJson = Configuration.Sample.json();
        JSONAssert.assertEquals(expectedJson, actualJson, true);
    }

    @Test
    void shouldSerializeConfigWithNestedConfigObjectCorrectly() {
        // Given
        TypeObject type2 = new TypeObject(ComponentNode2.class.getName());
        type2.set("property5", "my property 5 value");
        type2.set("property6", true);

        TypeObject type1 = new TypeObject(ComponentNode1.class.getName());
        type1.set(Config.id(), "38add40d-6a29-4e9e-9620-2bf165276204");
        type1.set(Config.title(), "Sample title");
        type1.set("property1", "my property 1 value");
        type1.set("property2", 473);
        type1.set("property3", true);
        type1.set("property4", type2);
        ConfigMetadata metadata = new ConfigMetadata(type1);

        // When
        String actualJson = ConfigSerializer.serialize(metadata);

        // Then
        String expectedJson = Configuration.NestedConfig.json();
        JSONAssert.assertEquals(expectedJson, actualJson, true);
    }
}