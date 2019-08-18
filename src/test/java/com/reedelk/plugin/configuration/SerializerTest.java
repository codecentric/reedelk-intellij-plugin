package com.reedelk.plugin.configuration;

import com.reedelk.plugin.fixture.ComponentNode1;
import com.reedelk.plugin.fixture.ComponentNode2;
import com.reedelk.plugin.fixture.ComponentNode3;
import com.reedelk.plugin.service.module.impl.ConfigMetadata;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import static com.reedelk.plugin.component.domain.TypeObjectDescriptor.TypeObject;
import static com.reedelk.plugin.fixture.Json.Configuration.NestedConfig;
import static com.reedelk.plugin.fixture.Json.Configuration.Sample;
import static com.reedelk.runtime.commons.JsonParser.Config;

class SerializerTest {

    @Test
    void shouldSerializeConfigCorrectly() {
        // Given
        TypeObject httpConfigType = new TypeObject(ComponentNode1.class.getName());
        httpConfigType.set(Config.id(), "38add40d-6a29-4e9e-9620-2bf165276204");
        httpConfigType.set(Config.title(), "HTTP Configuration");
        httpConfigType.set("host", "192.168.1.32");
        httpConfigType.set("port", 9190);
        httpConfigType.set("keepAlive", false);
        ConfigMetadata metadata = new ConfigMetadata(httpConfigType);

        // When
        String actualJson = Serializer.serialize(metadata);

        // Then
        String expectedJson = Sample.json();
        JSONAssert.assertEquals(expectedJson, actualJson, true);
    }

    @Test
    void shouldSerializeConfigWithNestedConfigObjectCorrectly() {
        // Given
        TypeObject keyStoreConfig = new TypeObject(ComponentNode3.class.getName());
        keyStoreConfig.set("algorithm", "SHA-1");

        TypeObject securityConfig = new TypeObject(ComponentNode2.class.getName());
        securityConfig.set("userName", "myUserName");
        securityConfig.set("password", "myPassword");
        securityConfig.set("keyStoreConfig", keyStoreConfig);

        TypeObject httpConfigType = new TypeObject(ComponentNode1.class.getName());
        httpConfigType.set(Config.id(), "38add40d-6a29-4e9e-9620-2bf165276204");
        httpConfigType.set(Config.title(), "HTTP Configuration");
        httpConfigType.set("host", "localhost");
        httpConfigType.set("port", 8182);
        httpConfigType.set("keepAlive", true);
        httpConfigType.set("securityConfig", securityConfig);
        ConfigMetadata metadata = new ConfigMetadata(httpConfigType);

        // When
        String actualJson = Serializer.serialize(metadata);

        // Then
        String expectedJson = NestedConfig.json();
        JSONAssert.assertEquals(expectedJson, actualJson, true);
    }

    @Test
    void shouldNotSerializeEmptyObjects() {
        // Given
        TypeObject keyStoreConfig = new TypeObject(ComponentNode3.class.getName());
        TypeObject securityConfig = new TypeObject(ComponentNode2.class.getName());
        securityConfig.set("keyStoreConfig", keyStoreConfig);

        TypeObject httpConfigType = new TypeObject(ComponentNode1.class.getName());
        httpConfigType.set(Config.id(), "38add40d-6a29-4e9e-9620-2bf165276204");
        httpConfigType.set(Config.title(), "HTTP Configuration");
        httpConfigType.set("host", "192.168.1.32");
        httpConfigType.set("port", 9190);
        httpConfigType.set("keepAlive", false);
        httpConfigType.set("securityConfig", securityConfig);
        ConfigMetadata metadata = new ConfigMetadata(httpConfigType);

        // When
        String actualJson = Serializer.serialize(metadata);

        // Then
        String expectedJson = Sample.json();
        JSONAssert.assertEquals(expectedJson, actualJson, true);
    }
}