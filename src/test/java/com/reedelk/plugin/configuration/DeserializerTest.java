package com.reedelk.plugin.configuration;

import com.reedelk.module.descriptor.model.ComponentDataHolder;
import com.reedelk.module.descriptor.model.ComponentPropertyDescriptor;
import com.reedelk.module.descriptor.model.TypeObjectDescriptor;
import com.reedelk.plugin.assertion.PluginAssertion;
import com.reedelk.plugin.component.deserializer.ConfigurationDeserializer;
import com.reedelk.plugin.fixture.ComponentNode1;
import com.reedelk.plugin.fixture.ComponentNode2;
import com.reedelk.plugin.fixture.ComponentNode3;
import com.reedelk.plugin.fixture.ComponentNode4;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static com.reedelk.plugin.component.type.generic.SamplePropertyDescriptors.Primitives;
import static com.reedelk.plugin.fixture.Json.Configuration.*;
import static com.reedelk.plugin.testutils.ObjectFactories.createTypeObjectDescriptor;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

class DeserializerTest {

    @Test
    void shouldCorrectlyDeserializeSimpleConfig() {
        // Given
        TypeObjectDescriptor httpConfigType = createTypeObjectDescriptor(
                ComponentNode1.class.getName(), asList(host, port, keepAlive));

        String json = Sample.json();

        // When
        Optional<ComponentDataHolder> deserialized = ConfigurationDeserializer.deserialize(json, httpConfigType);

        // Then
        assertThat(deserialized).isPresent();

        ComponentDataHolder dataHolder = deserialized.get();

        PluginAssertion.assertThat(dataHolder)
                .containsExactly("implementor", "id", "title", "host", "port", "keepAlive")
                .hasKeyWithValue("implementor", "com.reedelk.plugin.fixture.ComponentNode1")
                .hasKeyWithValue("id", "38add40d-6a29-4e9e-9620-2bf165276204")
                .hasKeyWithValue("title", "HTTP Configuration")
                .hasKeyWithValue("host", "192.168.1.32")
                .hasKeyWithValue("port", 9190)
                .hasKeyWithValue("keepAlive", true);
    }

    @Test
    void shouldCorrectlyDeserializeConfigWithNestedObjectProperties() {
        // Given
        TypeObjectDescriptor httpConfigType =
                createTypeObjectDescriptor(ComponentNode1.class.getName(),
                        asList(host, port, keepAlive, securityConfigPropertyDescriptor));

        String json = NestedConfig.json();

        // When
        Optional<ComponentDataHolder> deserialized =
                ConfigurationDeserializer.deserialize(json, httpConfigType);

        // Then
        assertThat(deserialized).isPresent();

        ComponentDataHolder dataHolder = deserialized.get();

        PluginAssertion.assertThat(dataHolder)
                .containsExactly("implementor", "id", "title",
                        "host", "port", "keepAlive", "securityConfig");

        TypeObjectDescriptor.TypeObject securityConfig = dataHolder.get("securityConfig");

        PluginAssertion.assertThat(securityConfig)
                .containsExactly("implementor", "userName", "password", "keyStoreConfig")
                .hasKeyWithValue("implementor", "com.reedelk.plugin.fixture.ComponentNode2")
                .hasKeyWithValue("userName", "myUserName")
                .hasKeyWithValue("password", "myPassword");

        TypeObjectDescriptor.TypeObject keyStoreConfig = securityConfig.get("keyStoreConfig");
        PluginAssertion.assertThat(keyStoreConfig)
                .containsExactly("implementor", "algorithm")
                .hasKeyWithValue("implementor", "com.reedelk.plugin.fixture.ComponentNode3")
                .hasKeyWithValue("algorithm", "SHA-1");
    }

    @Test
    void shouldCorrectlyDeserializeConfigWithNestedObjectPropertiesWhenNestedPropertyIsMissingInTheJson() {
        // Given
        TypeObjectDescriptor httpConfigType =
                createTypeObjectDescriptor(ComponentNode1.class.getName(),
                        asList(host, port, keepAlive, securityConfigPropertyDescriptor));


        String json = NestedConfigMissingObjectProperty.json();

        // When
        Optional<ComponentDataHolder> deserialized = ConfigurationDeserializer.deserialize(json, httpConfigType);

        // Then
        assertThat(deserialized).isPresent();

        ComponentDataHolder dataHolder = deserialized.get();

        PluginAssertion.assertThat(dataHolder)
                .containsExactly("implementor", "id", "title",
                        "host", "port", "keepAlive", "securityConfig");

        TypeObjectDescriptor.TypeObject nestedObject = dataHolder.get("securityConfig");
        PluginAssertion.assertThat(nestedObject)
                .containsExactly("implementor", "keyStoreConfig");
    }

    @Test
    void shouldCorrectlyDeserializeConfigWithNestedObjectPropertiesWhenNestedPropertyIsNullInTheJson() {
        // Given
        TypeObjectDescriptor httpConfigType =
                createTypeObjectDescriptor(ComponentNode1.class.getName(),
                        asList(host, port, keepAlive, securityConfigPropertyDescriptor));


        String json = NestedConfigNullObjectProperty.json();

        // When
        Optional<ComponentDataHolder> deserialized = ConfigurationDeserializer.deserialize(json, httpConfigType);

        // Then
        assertThat(deserialized).isPresent();

        ComponentDataHolder dataHolder = deserialized.get();

        PluginAssertion.assertThat(dataHolder)
                .containsExactly("implementor", "id", "title",
                        "host", "port", "keepAlive", "securityConfig");

        TypeObjectDescriptor.TypeObject nestedObject = dataHolder.get("securityConfig");
        PluginAssertion.assertThat(nestedObject)
                .containsExactly("implementor", "keyStoreConfig");
    }

    @Test
    void shouldReturnEmptyWhenJsonContainsErrors() {
        // Given
        TypeObjectDescriptor httpConfigType =
                createTypeObjectDescriptor(ComponentNode1.class.getName(),
                        asList(host, port, keepAlive, securityConfigPropertyDescriptor));


        String notValidJson = "myInvalidJson";

        // When
        Optional<ComponentDataHolder> deserialized = ConfigurationDeserializer.deserialize(notValidJson, httpConfigType);

        // Then
        assertThat(deserialized).isEmpty();
    }

    @Test
    void shouldReturnEmptyWhenJsonImplementorIsDifferentFromConfigPropertyFullyQualifiedName() {
        // Given
        TypeObjectDescriptor activeMqConfigType =
                // we provide an object descriptor with a different qualified name
                createTypeObjectDescriptor(ComponentNode4.class.getName(), asList(host, port, keepAlive));

        String json = Sample.json();

        // When
        Optional<ComponentDataHolder> deserialized = ConfigurationDeserializer.deserialize(json, activeMqConfigType);

        // Then
        assertThat(deserialized).isEmpty();
    }

    @Test
    void shouldCorrectlyDeserializeConfigWithoutTitle() {
        // Given
        TypeObjectDescriptor httpConfigType =
                createTypeObjectDescriptor(ComponentNode1.class.getName(), asList(host, port, keepAlive));

        String json = SampleWithoutTitle.json();

        // When
        Optional<ComponentDataHolder> deserialized = ConfigurationDeserializer.deserialize(json, httpConfigType);

        // Then
        assertThat(deserialized).isPresent();

        ComponentDataHolder dataHolder = deserialized.get();

        PluginAssertion.assertThat(dataHolder)
                .containsExactly("implementor", "id", "host", "port", "keepAlive")
                .hasKeyWithValue("implementor", "com.reedelk.plugin.fixture.ComponentNode1")
                .hasKeyWithValue("id", "38add40d-6a29-4e9e-9620-2bf165276204")
                .hasKeyWithValue("host", "192.168.1.32")
                .hasKeyWithValue("port", 9190)
                .hasKeyWithValue("keepAlive", true);
    }

    private final ComponentPropertyDescriptor host =
            ComponentPropertyDescriptor.builder()
                    .type(Primitives.stringTypeDescriptor)
                    .propertyName("host")
                    .displayName("Host")
                    .defaultValue("localhost")
                    .build();

    private final ComponentPropertyDescriptor port =
            ComponentPropertyDescriptor.builder()
                    .type(Primitives.integerTypeDescriptor)
                    .propertyName("port")
                    .displayName("Port")
                    .defaultValue("8080")
                    .build();

    private final ComponentPropertyDescriptor keepAlive =
            ComponentPropertyDescriptor.builder()
                    .type(Primitives.booleanTypeDescriptor)
                    .propertyName("keepAlive")
                    .displayName("Keep Alive")
                    .defaultValue("true")
                    .build();

    private final ComponentPropertyDescriptor userName =
            ComponentPropertyDescriptor.builder()
                    .type(Primitives.stringTypeDescriptor)
                    .propertyName("userName")
                    .displayName("User Name")
                    .build();

    private final ComponentPropertyDescriptor password =
            ComponentPropertyDescriptor.builder()
                    .type(Primitives.stringTypeDescriptor)
                    .propertyName("password")
                    .displayName("Password")
                    .build();

    private final ComponentPropertyDescriptor algorithm =
            ComponentPropertyDescriptor.builder()
                    .type(Primitives.stringTypeDescriptor)
                    .propertyName("algorithm")
                    .displayName("Algorithm")
                    .build();

    private final TypeObjectDescriptor keyStoreConfigObjectType = createTypeObjectDescriptor(
            ComponentNode3.class.getName(), singletonList(algorithm));

    private final ComponentPropertyDescriptor keyStoreConfig =
            ComponentPropertyDescriptor.builder()
                    .type(keyStoreConfigObjectType)
                    .propertyName("keyStoreConfig")
                    .displayName("Key Store config")
                    .build();

    private final TypeObjectDescriptor securityConfigObjectType = createTypeObjectDescriptor(
            ComponentNode2.class.getName(), asList(userName, password, keyStoreConfig));

    private final ComponentPropertyDescriptor securityConfigPropertyDescriptor =
            ComponentPropertyDescriptor.builder()
                    .type(securityConfigObjectType)
                    .propertyName("securityConfig")
                    .displayName("Security Config")
                    .build();
}