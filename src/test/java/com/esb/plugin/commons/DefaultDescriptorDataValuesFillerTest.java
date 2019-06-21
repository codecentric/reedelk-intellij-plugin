package com.esb.plugin.commons;

import com.esb.plugin.component.domain.ComponentDataHolder;
import com.esb.plugin.component.domain.ComponentPropertyDescriptor;
import com.esb.plugin.component.domain.TypeObjectDescriptor;
import com.esb.plugin.component.domain.TypePrimitiveDescriptor;
import com.esb.plugin.graph.serializer.JsonObjectFactory;
import com.esb.plugin.service.module.impl.ConfigMetadata;
import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

class DefaultDescriptorDataValuesFillerTest {

    private final TypePrimitiveDescriptor stringTypeDescriptor =
            new TypePrimitiveDescriptor(String.class);

    private final TypePrimitiveDescriptor intTypeDescriptor =
            new TypePrimitiveDescriptor(int.class);

    private ComponentDataHolder testDataHolder;

    @BeforeEach
    void setUp() {
        testDataHolder = new ConfigMetadata(JsonObjectFactory.newJSONObject());
    }

    @Test
    void shouldCorrectlyFillDefaultValuesForPrimitiveTypes() {
        // Given
        ComponentPropertyDescriptor namePropertyDescriptor =
                new ComponentPropertyDescriptor("name", stringTypeDescriptor, "Your name", "Test name");

        ComponentPropertyDescriptor surnamePropertyDescriptor =
                new ComponentPropertyDescriptor("surname", stringTypeDescriptor, "Your surname", "Test surname");

        // When
        DefaultDescriptorDataValuesFiller
                .fill(testDataHolder, asList(namePropertyDescriptor, surnamePropertyDescriptor));

        // Then
        assertThat(testDataHolder.keys()).containsExactlyInAnyOrder("name", "surname");

        String defaultName = testDataHolder.get("name");
        assertThat(defaultName).isEqualTo("Test name");

        String defaultSurname = testDataHolder.get("surname");
        assertThat(defaultSurname).isEqualTo("Test surname");
    }

    @Test
    void shouldCorrectlyFillNullDefaultValueForUndefinedDefaultValue() {
        // Given
        ComponentPropertyDescriptor addressPropertyDescriptorWithoutDefaultValue =
                new ComponentPropertyDescriptor("address", stringTypeDescriptor, "Address");

        // When
        DefaultDescriptorDataValuesFiller
                .fill(testDataHolder, ImmutableList.of(addressPropertyDescriptorWithoutDefaultValue));

        // Then
        String defaultAddress = testDataHolder.get("address");
        assertThat(defaultAddress).isNull();
    }

    @Test
    void shouldCorrectlyFillDefaultValuesForComplexTypes() {
        // Given
        ComponentPropertyDescriptor nestedProperty1 =
                new ComponentPropertyDescriptor("nestedProperty1", stringTypeDescriptor, "Nested property 1", "Nested one");
        ComponentPropertyDescriptor nestedProperty2 =
                new ComponentPropertyDescriptor("nestedProperty2", intTypeDescriptor, "Nested property 2", 234);

        String objectFullyQualifiedName = "com.esb.test.Component";
        TypeObjectDescriptor typeObjectDescriptor =
                new TypeObjectDescriptor(objectFullyQualifiedName, false, asList(nestedProperty1, nestedProperty2));

        ComponentPropertyDescriptor objectPropertyDescriptor =
                new ComponentPropertyDescriptor("configuration", typeObjectDescriptor, "Configuration");

        // When
        DefaultDescriptorDataValuesFiller
                .fill(testDataHolder, Collections.singletonList(objectPropertyDescriptor));

        // Then
        TypeObjectDescriptor.TypeObject configuration = testDataHolder.get("configuration");
        String defaultNestedProperty1 = configuration.get("nestedProperty1");
        int defaultNestedProperty2 = configuration.get("nestedProperty2");

        assertThat(defaultNestedProperty1).isEqualTo("Nested one");
        assertThat(defaultNestedProperty2).isEqualTo(234);
    }
}