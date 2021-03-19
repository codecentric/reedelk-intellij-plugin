package de.codecentric.reedelk.plugin.configuration;

import de.codecentric.reedelk.module.descriptor.model.property.ObjectDescriptor;
import de.codecentric.reedelk.module.descriptor.model.property.PropertyDescriptor;
import de.codecentric.reedelk.module.descriptor.model.property.Shared;
import de.codecentric.reedelk.plugin.commons.TypeObjectFactory;
import de.codecentric.reedelk.plugin.component.serializer.ConfigurationSerializer;
import de.codecentric.reedelk.plugin.component.type.generic.SamplePropertyDescriptors;
import de.codecentric.reedelk.plugin.fixture.ComponentNode1;
import de.codecentric.reedelk.plugin.fixture.ComponentNode2;
import de.codecentric.reedelk.plugin.fixture.ComponentNode3;
import de.codecentric.reedelk.plugin.fixture.Json;
import de.codecentric.reedelk.plugin.service.module.impl.configuration.ConfigMetadata;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.util.Collections;

import static de.codecentric.reedelk.module.descriptor.model.property.ObjectDescriptor.TypeObject;
import static de.codecentric.reedelk.plugin.testutils.ObjectFactories.createTypeObjectDescriptor;
import static de.codecentric.reedelk.runtime.commons.JsonParser.Config;
import static de.codecentric.reedelk.runtime.commons.JsonParser.Implementor;
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
        String expectedJson = Json.Configuration.Sample.json();
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
        String expectedJson = Json.Configuration.NestedConfig.json();
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
        String expectedJson = Json.Configuration.SampleWithEmptyConfig.json();
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
        String expectedJson = Json.Configuration.SampleWithoutBoolean.json();
        JSONAssert.assertEquals(expectedJson, actualJson, true);
    }

    private PropertyDescriptor hostProperty =
            PropertyDescriptor.builder()
                    .type(SamplePropertyDescriptors.Primitives.stringTypeDescriptor)
                    .name("host")
                    .displayName("Host")
                    .build();

    private PropertyDescriptor portProperty =
            PropertyDescriptor.builder()
                    .type(SamplePropertyDescriptors.Primitives.integerTypeDescriptor)
                    .name("port")
                    .displayName("Port")
                    .build();

    private PropertyDescriptor keepAlive =
            PropertyDescriptor.builder()
                    .type(SamplePropertyDescriptors.Primitives.booleanTypeDescriptor)
                    .name("keepAlive")
                    .displayName("Keep Alive")
                    .build();


    private PropertyDescriptor algorithm =
            PropertyDescriptor.builder()
                    .type(SamplePropertyDescriptors.Primitives.stringTypeDescriptor)
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
                    .type(SamplePropertyDescriptors.Primitives.stringTypeDescriptor)
                    .name("userName")
                    .displayName("User Name")
                    .build();

    private PropertyDescriptor password =
            PropertyDescriptor.builder()
                    .type(SamplePropertyDescriptors.Primitives.stringTypeDescriptor)
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
