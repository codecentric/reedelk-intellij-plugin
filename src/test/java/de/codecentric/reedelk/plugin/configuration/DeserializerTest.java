package de.codecentric.reedelk.plugin.configuration;

import de.codecentric.reedelk.module.descriptor.model.component.ComponentDataHolder;
import de.codecentric.reedelk.module.descriptor.model.property.ObjectDescriptor;
import de.codecentric.reedelk.module.descriptor.model.property.PropertyDescriptor;
import de.codecentric.reedelk.plugin.assertion.PluginAssertion;
import de.codecentric.reedelk.plugin.component.type.generic.SamplePropertyDescriptors;
import de.codecentric.reedelk.plugin.fixture.*;
import de.codecentric.reedelk.plugin.component.deserializer.ConfigurationDeserializer;
import de.codecentric.reedelk.plugin.fixture.ComponentNode1;
import de.codecentric.reedelk.plugin.fixture.ComponentNode2;
import de.codecentric.reedelk.plugin.fixture.ComponentNode3;
import de.codecentric.reedelk.plugin.fixture.ComponentNode4;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static de.codecentric.reedelk.plugin.testutils.ObjectFactories.createTypeObjectDescriptor;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

class DeserializerTest {

    @Test
    void shouldCorrectlyDeserializeSimpleConfig() {
        // Given
        ObjectDescriptor httpConfigType = createTypeObjectDescriptor(
                ComponentNode1.class.getName(), asList(host, port, keepAlive));

        String json = Json.Configuration.Sample.json();

        // When
        Optional<ComponentDataHolder> deserialized = ConfigurationDeserializer.deserialize(json, httpConfigType);

        // Then
        assertThat(deserialized).isPresent();

        ComponentDataHolder dataHolder = deserialized.get();

        PluginAssertion.assertThat(dataHolder)
                .containsExactly("implementor", "id", "title", "host", "port", "keepAlive")
                .hasKeyWithValue("implementor", "de.codecentric.reedelk.plugin.fixture.ComponentNode1")
                .hasKeyWithValue("id", "38add40d-6a29-4e9e-9620-2bf165276204")
                .hasKeyWithValue("title", "HTTP Configuration")
                .hasKeyWithValue("host", "192.168.1.32")
                .hasKeyWithValue("port", 9190)
                .hasKeyWithValue("keepAlive", true);
    }

    @Test
    void shouldCorrectlyDeserializeConfigWithNestedObjectProperties() {
        // Given
        ObjectDescriptor httpConfigType =
                createTypeObjectDescriptor(ComponentNode1.class.getName(),
                        asList(host, port, keepAlive, securityConfigPropertyDescriptor));

        String json = Json.Configuration.NestedConfig.json();

        // When
        Optional<ComponentDataHolder> deserialized =
                ConfigurationDeserializer.deserialize(json, httpConfigType);

        // Then
        assertThat(deserialized).isPresent();

        ComponentDataHolder dataHolder = deserialized.get();

        PluginAssertion.assertThat(dataHolder)
                .containsExactly("implementor", "id", "title",
                        "host", "port", "keepAlive", "securityConfig");

        ObjectDescriptor.TypeObject securityConfig = dataHolder.get("securityConfig");

        PluginAssertion.assertThat(securityConfig)
                .containsExactly("implementor", "userName", "password", "keyStoreConfig")
                .hasKeyWithValue("implementor", "de.codecentric.reedelk.plugin.fixture.ComponentNode2")
                .hasKeyWithValue("userName", "myUserName")
                .hasKeyWithValue("password", "myPassword");

        ObjectDescriptor.TypeObject keyStoreConfig = securityConfig.get("keyStoreConfig");
        PluginAssertion.assertThat(keyStoreConfig)
                .containsExactly("implementor", "algorithm")
                .hasKeyWithValue("implementor", "de.codecentric.reedelk.plugin.fixture.ComponentNode3")
                .hasKeyWithValue("algorithm", "SHA-1");
    }

    @Test
    void shouldCorrectlyDeserializeConfigWithNestedObjectPropertiesWhenNestedPropertyIsMissingInTheJson() {
        // Given
        ObjectDescriptor httpConfigType =
                createTypeObjectDescriptor(ComponentNode1.class.getName(),
                        asList(host, port, keepAlive, securityConfigPropertyDescriptor));


        String json = Json.Configuration.NestedConfigMissingObjectProperty.json();

        // When
        Optional<ComponentDataHolder> deserialized = ConfigurationDeserializer.deserialize(json, httpConfigType);

        // Then
        assertThat(deserialized).isPresent();

        ComponentDataHolder dataHolder = deserialized.get();

        PluginAssertion.assertThat(dataHolder)
                .containsExactly("implementor", "id", "title",
                        "host", "port", "keepAlive", "securityConfig");

        ObjectDescriptor.TypeObject nestedObject = dataHolder.get("securityConfig");
        PluginAssertion.assertThat(nestedObject)
                .containsExactly("implementor", "keyStoreConfig");
    }

    @Test
    void shouldCorrectlyDeserializeConfigWithNestedObjectPropertiesWhenNestedPropertyIsNullInTheJson() {
        // Given
        ObjectDescriptor httpConfigType =
                createTypeObjectDescriptor(ComponentNode1.class.getName(),
                        asList(host, port, keepAlive, securityConfigPropertyDescriptor));


        String json = Json.Configuration.NestedConfigNullObjectProperty.json();

        // When
        Optional<ComponentDataHolder> deserialized = ConfigurationDeserializer.deserialize(json, httpConfigType);

        // Then
        assertThat(deserialized).isPresent();

        ComponentDataHolder dataHolder = deserialized.get();

        PluginAssertion.assertThat(dataHolder)
                .containsExactly("implementor", "id", "title",
                        "host", "port", "keepAlive", "securityConfig");

        ObjectDescriptor.TypeObject nestedObject = dataHolder.get("securityConfig");
        PluginAssertion.assertThat(nestedObject)
                .containsExactly("implementor", "keyStoreConfig");
    }

    @Test
    void shouldReturnEmptyWhenJsonContainsErrors() {
        // Given
        ObjectDescriptor httpConfigType =
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
        ObjectDescriptor activeMqConfigType =
                // we provide an object descriptor with a different qualified name
                createTypeObjectDescriptor(ComponentNode4.class.getName(), asList(host, port, keepAlive));

        String json = Json.Configuration.Sample.json();

        // When
        Optional<ComponentDataHolder> deserialized = ConfigurationDeserializer.deserialize(json, activeMqConfigType);

        // Then
        assertThat(deserialized).isEmpty();
    }

    @Test
    void shouldCorrectlyDeserializeConfigWithoutTitle() {
        // Given
        ObjectDescriptor httpConfigType =
                createTypeObjectDescriptor(ComponentNode1.class.getName(), asList(host, port, keepAlive));

        String json = Json.Configuration.SampleWithoutTitle.json();

        // When
        Optional<ComponentDataHolder> deserialized = ConfigurationDeserializer.deserialize(json, httpConfigType);

        // Then
        assertThat(deserialized).isPresent();

        ComponentDataHolder dataHolder = deserialized.get();

        PluginAssertion.assertThat(dataHolder)
                .containsExactly("implementor", "id", "host", "port", "keepAlive")
                .hasKeyWithValue("implementor", "de.codecentric.reedelk.plugin.fixture.ComponentNode1")
                .hasKeyWithValue("id", "38add40d-6a29-4e9e-9620-2bf165276204")
                .hasKeyWithValue("host", "192.168.1.32")
                .hasKeyWithValue("port", 9190)
                .hasKeyWithValue("keepAlive", true);
    }

    private final PropertyDescriptor host =
            PropertyDescriptor.builder()
                    .type(SamplePropertyDescriptors.Primitives.stringTypeDescriptor)
                    .name("host")
                    .displayName("Host")
                    .initValue("localhost")
                    .build();

    private final PropertyDescriptor port =
            PropertyDescriptor.builder()
                    .type(SamplePropertyDescriptors.Primitives.integerTypeDescriptor)
                    .name("port")
                    .displayName("Port")
                    .initValue("8080")
                    .build();

    private final PropertyDescriptor keepAlive =
            PropertyDescriptor.builder()
                    .type(SamplePropertyDescriptors.Primitives.booleanTypeDescriptor)
                    .name("keepAlive")
                    .displayName("Keep Alive")
                    .initValue("true")
                    .build();

    private final PropertyDescriptor userName =
            PropertyDescriptor.builder()
                    .type(SamplePropertyDescriptors.Primitives.stringTypeDescriptor)
                    .name("userName")
                    .displayName("User Name")
                    .build();

    private final PropertyDescriptor password =
            PropertyDescriptor.builder()
                    .type(SamplePropertyDescriptors.Primitives.stringTypeDescriptor)
                    .name("password")
                    .displayName("Password")
                    .build();

    private final PropertyDescriptor algorithm =
            PropertyDescriptor.builder()
                    .type(SamplePropertyDescriptors.Primitives.stringTypeDescriptor)
                    .name("algorithm")
                    .displayName("Algorithm")
                    .build();

    private final ObjectDescriptor keyStoreConfigObjectType = createTypeObjectDescriptor(
            ComponentNode3.class.getName(), singletonList(algorithm));

    private final PropertyDescriptor keyStoreConfig =
            PropertyDescriptor.builder()
                    .type(keyStoreConfigObjectType)
                    .name("keyStoreConfig")
                    .displayName("Key Store config")
                    .build();

    private final ObjectDescriptor securityConfigObjectType = createTypeObjectDescriptor(
            ComponentNode2.class.getName(), asList(userName, password, keyStoreConfig));

    private final PropertyDescriptor securityConfigPropertyDescriptor =
            PropertyDescriptor.builder()
                    .type(securityConfigObjectType)
                    .name("securityConfig")
                    .displayName("Security Config")
                    .build();
}
