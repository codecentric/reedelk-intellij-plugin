package com.reedelk.plugin.configuration;

import com.reedelk.plugin.component.domain.ComponentDataHolder;
import com.reedelk.plugin.component.domain.ComponentPropertyDescriptor;
import com.reedelk.plugin.component.domain.TypeObjectDescriptor;
import com.reedelk.plugin.fixture.ComponentNode1;
import com.reedelk.plugin.fixture.ComponentNode2;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static com.reedelk.plugin.component.type.generic.SamplePropertyDescriptors.*;
import static com.reedelk.plugin.fixture.Json.Configuration;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

class DeserializerTest {

    @Test
    void shouldCorrectlyDeserializeSimpleConfig() {
        // Given
        TypeObjectDescriptor componentNode1Type =
                new TypeObjectDescriptor(ComponentNode1.class.getName(), false,
                        asList(property1, property2, property3));

        ComponentPropertyDescriptor componentNode1Property =
                ComponentPropertyDescriptor.builder()
                        .propertyName("componentNode1Property")
                        .type(componentNode1Type)
                        .displayName("Sample component node 1 config")
                        .build();

        String json = Configuration.Sample.json();

        // When
        Optional<ComponentDataHolder> deserialized = Deserializer.deserialize(json, componentNode1Property);

        // Then
        assertThat(deserialized).isPresent();

        ComponentDataHolder dataHolder = deserialized.get();
        assertThat(dataHolder.keys()).containsExactlyInAnyOrder(
                "implementor",
                "id",
                "title",
                "property1",
                "property2",
                "property3");


        assertThat((String) dataHolder.get("implementor")).isEqualTo("com.reedelk.plugin.fixture.ComponentNode1");
        assertThat((String) dataHolder.get("id")).isEqualTo("38add40d-6a29-4e9e-9620-2bf165276204");
        assertThat((String) dataHolder.get("title")).isEqualTo("Sample title");
        assertThat((String) dataHolder.get("property1")).isEqualTo("my property 1 value");
        assertThat((Integer) dataHolder.get("property2")).isEqualTo(473);
        assertThat((Boolean) dataHolder.get("property3")).isEqualTo(true);
    }

    @Test
    void shouldCorrectlyDeserializeConfigWithNestedObjectProperties() {
        // Given
        TypeObjectDescriptor componentNode1Type =
                new TypeObjectDescriptor(ComponentNode1.class.getName(), false,
                        asList(property1, property2, property3, property4));

        ComponentPropertyDescriptor componentNode1Property =
                ComponentPropertyDescriptor.builder()
                        .propertyName("componentNode1Property")
                        .type(componentNode1Type)
                        .displayName("Sample component node 1 config")
                        .build();

        String json = Configuration.NestedConfig.json();

        // When
        Optional<ComponentDataHolder> deserialized =
                Deserializer.deserialize(json, componentNode1Property);

        // Then
        assertThat(deserialized).isPresent();

        ComponentDataHolder dataHolder = deserialized.get();
        assertThat(dataHolder.keys()).containsExactlyInAnyOrder(
                "implementor",
                "id",
                "title",
                "property1",
                "property2",
                "property3",
                "property4");

        TypeObjectDescriptor.TypeObject nestedObject = dataHolder.get("property4");
        assertThat((String) nestedObject.get("property5")).isEqualTo("my property 5 value");
        assertThat((Boolean) nestedObject.get("property6")).isEqualTo(true);
    }

    @Test
    void shouldCorrectlyDeserializeConfigWithNestedObjectPropertiesWhenNestedPropertyIsMissingInTheJson() {
        // Given
        TypeObjectDescriptor componentNode1Type =
                new TypeObjectDescriptor(ComponentNode1.class.getName(), false,
                        asList(property1, property2, property3, property4));

        ComponentPropertyDescriptor componentNode1Property =
                ComponentPropertyDescriptor.builder()
                        .propertyName("componentNode1Property")
                        .type(componentNode1Type)
                        .displayName("Sample component node 1 config")
                        .build();

        String json = Configuration.NestedConfigMissingNestedObjectProperty.json();

        // When
        Optional<ComponentDataHolder> deserialized = Deserializer.deserialize(json, componentNode1Property);

        // Then: we expect the nested object to be not null!
        assertThat(deserialized).isPresent();

        ComponentDataHolder dataHolder = deserialized.get();
        assertThat(dataHolder.keys()).containsExactlyInAnyOrder(
                "implementor",
                "id",
                "title",
                "property1",
                "property2",
                "property3",
                "property4");

        TypeObjectDescriptor.TypeObject nestedObject = dataHolder.get("property4");
        assertThat(nestedObject).isNotNull();
    }

    private final ComponentPropertyDescriptor property1 =
            ComponentPropertyDescriptor.builder()
                    .propertyName("property1")
                    .type(stringTypeDescriptor)
                    .displayName("Property 1")
                    .defaultValue("")
                    .build();

    private final ComponentPropertyDescriptor property2 =
            ComponentPropertyDescriptor.builder()
                    .propertyName("property2")
                    .type(integerTypeDescriptor)
                    .displayName("Property 2")
                    .defaultValue("0")
                    .build();

    private final ComponentPropertyDescriptor property3 =
            ComponentPropertyDescriptor.builder()
                    .propertyName("property3")
                    .type(booleanTypeDescriptor)
                    .displayName("Property 3")
                    .defaultValue("true")
                    .build();

    private final ComponentPropertyDescriptor property5 =
            ComponentPropertyDescriptor.builder()
                    .propertyName("property5")
                    .type(stringTypeDescriptor)
                    .displayName("Property 5")
                    .build();

    private final ComponentPropertyDescriptor property6 =
            ComponentPropertyDescriptor.builder()
                    .propertyName("property6")
                    .type(booleanTypeDescriptor)
                    .displayName("Property 6")
                    .defaultValue("true")
                    .build();

    private final TypeObjectDescriptor componentNode2Type =
            new TypeObjectDescriptor(ComponentNode2.class.getName(), false,
                    asList(property5, property6));

    private final ComponentPropertyDescriptor property4 =
            ComponentPropertyDescriptor.builder()
                    .propertyName("property4")
                    .type(componentNode2Type)
                    .displayName("Property 4")
                    .build();
}