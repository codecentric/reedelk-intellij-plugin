package com.reedelk.plugin.configuration;

import com.reedelk.module.descriptor.model.property.ObjectDescriptor;
import com.reedelk.module.descriptor.model.property.PropertyDescriptor;
import com.reedelk.module.descriptor.model.property.Shared;
import com.reedelk.plugin.commons.TypeObjectFactory;
import com.reedelk.plugin.component.serializer.ConfigurationSerializer;
import com.reedelk.plugin.fixture.ComponentNode1;
import com.reedelk.plugin.fixture.ComponentNode2;
import com.reedelk.plugin.fixture.ComponentNode3;
import com.reedelk.plugin.service.module.impl.configuration.ConfigMetadata;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.util.Collections;

import static com.reedelk.module.descriptor.model.property.ObjectDescriptor.TypeObject;
import static com.reedelk.plugin.component.type.generic.SamplePropertyDescriptors.Primitives;
import static com.reedelk.plugin.fixture.Json.Configuration.*;
import static com.reedelk.plugin.testutils.ObjectFactories.createTypeObjectDescriptor;
import static com.reedelk.runtime.commons.JsonParser.Config;
import static com.reedelk.runtime.commons.JsonParser.Implementor;
import static java.util.Arrays.asList;

class SerializerTest {

    @Test
    void shouldSerializeConfigCorrectly() {
        // Given
        TypeObject httpConfigType = TypeObjectFactory.newInstance();
        httpConfigType.set(Implementor.name(), ComponentNode1.class.getName());
        httpConfigType.set(Config.id(), "38add40d-6a29-4e9e-9620-2bf165276204");
        httpConfigType.set(Config.title(), "HTTP Configuration");
        httpConfigType.set("host", "192.168.1.32");
        httpConfigType.set("port", 9190);
        httpConfigType.set("keepAlive", true);

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
        TypeObject keyStoreConfig = TypeObjectFactory.newInstance();
        keyStoreConfig.set(Implementor.name(), ComponentNode3.class.getName());
        keyStoreConfig.set("algorithm", "SHA-1");

        TypeObject securityConfig = TypeObjectFactory.newInstance();
        securityConfig.set(Implementor.name(), ComponentNode2.class.getName());
        securityConfig.set("userName", "myUserName");
        securityConfig.set("password", "myPassword");
        securityConfig.set("keyStoreConfig", keyStoreConfig);

        TypeObject httpConfigType = TypeObjectFactory.newInstance();
        httpConfigType.set(Implementor.name(), ComponentNode1.class.getName());
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
    void shouldNotSerializeEmptyObjects() {
        // Given
        TypeObject keyStoreConfig = TypeObjectFactory.newInstance();
        keyStoreConfig.set(Implementor.name(), ComponentNode3.class.getName());
        TypeObject securityConfig = TypeObjectFactory.newInstance();
        securityConfig.set(Implementor.name(), ComponentNode2.class.getName());
        securityConfig.set("keyStoreConfig", keyStoreConfig);

        TypeObject httpConfigType = TypeObjectFactory.newInstance();
        httpConfigType.set(Implementor.name(), ComponentNode1.class.getName());
        httpConfigType.set(Config.id(), "38add40d-6a29-4e9e-9620-2bf165276204");
        httpConfigType.set(Config.title(), "HTTP Configuration");
        httpConfigType.set("host", "192.168.1.32");
        httpConfigType.set("port", 9190);
        httpConfigType.set("keepAlive", true);
        httpConfigType.set("securityConfig", securityConfig);


        ConfigMetadata metadata = new ConfigMetadata(httpConfigType, configObjectDescriptor);

        // When
        String actualJson = ConfigurationSerializer.serialize(metadata);

        // Then
        String expectedJson = SampleWithEmptyConfig.json();
        JSONAssert.assertEquals(expectedJson, actualJson, true);
    }

    @Test
    void shouldNotSerializeBooleanWhenValueIsFalse() {
        // Given
        TypeObject httpConfigType = TypeObjectFactory.newInstance();
        httpConfigType.set(Implementor.name(), ComponentNode1.class.getName());
        httpConfigType.set(Config.id(), "38add40d-6a29-4e9e-9620-2bf165276204");
        httpConfigType.set(Config.title(), "HTTP Configuration");
        httpConfigType.set("host", "192.168.1.32");
        httpConfigType.set("port", 9190);
        httpConfigType.set("keepAlive", false);

        ConfigMetadata metadata = new ConfigMetadata(httpConfigType, configObjectDescriptor);

        // When
        String actualJson = ConfigurationSerializer.serialize(metadata);

        // Then
        String expectedJson = SampleWithoutBoolean.json();
        JSONAssert.assertEquals(expectedJson, actualJson, true);
    }

    private PropertyDescriptor hostProperty =
            PropertyDescriptor.builder()
                    .type(Primitives.stringTypeDescriptor)
                    .name("host")
                    .displayName("Host")
                    .build();

    private PropertyDescriptor portProperty =
            PropertyDescriptor.builder()
                    .type(Primitives.integerTypeDescriptor)
                    .name("port")
                    .displayName("Port")
                    .build();

    private PropertyDescriptor keepAlive =
            PropertyDescriptor.builder()
                    .type(Primitives.booleanTypeDescriptor)
                    .name("keepAlive")
                    .displayName("Keep Alive")
                    .build();


    private PropertyDescriptor algorithm =
            PropertyDescriptor.builder()
                    .type(Primitives.stringTypeDescriptor)
                    .name("algorithm")
                    .displayName("Algorithm")
                    .build();

    private ObjectDescriptor keyStoreConfigObjectType = createTypeObjectDescriptor(
            ComponentNode3.class.getName(), Collections.singletonList(algorithm));

    private PropertyDescriptor keyStoreConfigDescriptor =
            PropertyDescriptor.builder()
                    .type(keyStoreConfigObjectType)
                    .name("keyStoreConfig")
                    .displayName("Key store config")
                    .build();

    private PropertyDescriptor userName =
            PropertyDescriptor.builder()
                    .type(Primitives.stringTypeDescriptor)
                    .name("userName")
                    .displayName("User Name")
                    .build();

    private PropertyDescriptor password =
            PropertyDescriptor.builder()
                    .type(Primitives.stringTypeDescriptor)
                    .name("password")
                    .displayName("Password")
                    .build();


    private ObjectDescriptor securityConfigObjectType = createTypeObjectDescriptor(ComponentNode2.class.getName(),
            asList(userName, password, keyStoreConfigDescriptor));

    private PropertyDescriptor securityConfigDescriptor =
            PropertyDescriptor.builder()
                    .type(securityConfigObjectType)
                    .name("securityConfig")
                    .displayName("Security config")
                    .build();

    private ObjectDescriptor configObjectDescriptor = createTypeObjectDescriptor(
            ComponentNode1.class.getName(), asList(hostProperty, portProperty, keepAlive, securityConfigDescriptor), Shared.YES);
}
