package com.reedelk.plugin.configuration;

import com.reedelk.plugin.component.domain.*;
import com.reedelk.plugin.component.serializer.ConfigurationSerializer;
import com.reedelk.plugin.fixture.ComponentNode1;
import com.reedelk.plugin.fixture.ComponentNode2;
import com.reedelk.plugin.fixture.ComponentNode3;
import com.reedelk.plugin.service.module.impl.ConfigMetadata;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.util.Arrays;
import java.util.Collections;

import static com.reedelk.plugin.component.domain.TypeObjectDescriptor.TypeObject;
import static com.reedelk.plugin.component.type.generic.SamplePropertyDescriptors.*;
import static com.reedelk.plugin.fixture.Json.Configuration.*;
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

        ConfigMetadata metadata = new ConfigMetadata(httpConfigType, configObjectDescriptor);

        // When
        String actualJson = ConfigurationSerializer.serialize(metadata);

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


        ConfigMetadata metadata = new ConfigMetadata(httpConfigType, configObjectDescriptor);

        // When
        String actualJson = ConfigurationSerializer.serialize(metadata);

        // Then
        String expectedJson = NestedConfig.json();
        JSONAssert.assertEquals(expectedJson, actualJson, true);
    }

    @Test
    void shouldSerializeEmptyObjects() {
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


        ConfigMetadata metadata = new ConfigMetadata(httpConfigType, configObjectDescriptor);

        // When
        String actualJson = ConfigurationSerializer.serialize(metadata);

        // Then
        String expectedJson = SampleWithEmptyConfig.json();
        JSONAssert.assertEquals(expectedJson, actualJson, true);
    }

    private ComponentPropertyDescriptor hostProperty =
            ComponentPropertyDescriptor.builder()
                    .type(stringTypeDescriptor)
                    .isTransient(Transient.NO)
                    .propertyName("host")
                    .displayName("Host")
                    .build();

    private ComponentPropertyDescriptor portProperty =
            ComponentPropertyDescriptor.builder()
                    .type(integerTypeDescriptor)
                    .isTransient(Transient.NO)
                    .propertyName("port")
                    .displayName("Port")
                    .build();

    private ComponentPropertyDescriptor keepAlive =
            ComponentPropertyDescriptor.builder()
                    .type(booleanTypeDescriptor)
                    .isTransient(Transient.NO)
                    .propertyName("keepAlive")
                    .displayName("Keep Alive")
                    .build();


    private ComponentPropertyDescriptor algorithm =
            ComponentPropertyDescriptor.builder()
                    .type(stringTypeDescriptor)
                    .isTransient(Transient.NO)
                    .propertyName("algorithm")
                    .displayName("Algorithm")
                    .build();

    private TypeObjectDescriptor keyStoreConfigObjectType = new TypeObjectDescriptor(ComponentNode3.class.getName(),
            Collections.singletonList(algorithm),
            Shared.NO,
            Collapsible.NO);

    private ComponentPropertyDescriptor keyStoreConfigDescriptor =
            ComponentPropertyDescriptor.builder()
                    .type(keyStoreConfigObjectType)
                    .isTransient(Transient.NO)
                    .propertyName("keyStoreConfig")
                    .displayName("Key store config")
                    .build();

    private ComponentPropertyDescriptor userName =
            ComponentPropertyDescriptor.builder()
                    .type(stringTypeDescriptor)
                    .isTransient(Transient.NO)
                    .propertyName("userName")
                    .displayName("User Name")
                    .build();

    private ComponentPropertyDescriptor password =
            ComponentPropertyDescriptor.builder()
                    .type(stringTypeDescriptor)
                    .isTransient(Transient.NO)
                    .propertyName("password")
                    .displayName("Password")
                    .build();


    private TypeObjectDescriptor securityConfigObjectType = new TypeObjectDescriptor(ComponentNode2.class.getName(),
            Arrays.asList(userName, password, keyStoreConfigDescriptor),
            Shared.NO,
            Collapsible.NO);

    private ComponentPropertyDescriptor securityConfigDescriptor =
            ComponentPropertyDescriptor.builder()
                    .type(securityConfigObjectType)
                    .isTransient(Transient.NO)
                    .propertyName("securityConfig")
                    .displayName("Security config")
                    .build();

    private TypeObjectDescriptor configObjectDescriptor = new TypeObjectDescriptor(
            ComponentNode1.class.getName(),
            Arrays.asList(hostProperty, portProperty, keepAlive, securityConfigDescriptor),
            Shared.YES,
            Collapsible.NO);
}