package com.esb.plugin.commons;

import com.esb.plugin.component.domain.ComponentDataHolder;
import com.esb.plugin.component.domain.ComponentPropertyDescriptor;
import com.esb.plugin.component.domain.TypeObjectDescriptor;
import com.esb.plugin.component.domain.TypePrimitiveDescriptor;
import com.esb.plugin.service.module.impl.ConfigMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.esb.internal.commons.JsonParser.Component;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

class DefaultDescriptorDataValuesFillerTest {

    private ComponentDataHolder testDataHolder;

    @BeforeEach
    void setUp() {
        testDataHolder = new ConfigMetadata(new TypeObjectDescriptor.TypeObject());
    }

    @Test
    void shouldCorrectlyFillDefaultValuesForPrimitiveTypes() {
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
                .fill(testDataHolder, singletonList(addressPropertyDescriptorWithoutDefaultValue));

        // Then
        String defaultAddress = testDataHolder.get("address");
        assertThat(defaultAddress).isNull();
    }

    @Test
    void shouldCorrectlyFillDefaultValuesForComplexTypes() {
        // Given
        String objectFullyQualifiedName = "com.esb.test.Component";

        TypeObjectDescriptor typeObjectDescriptor =
                new TypeObjectDescriptor(objectFullyQualifiedName, false, asList(namePropertyDescriptor, zipCodePropertyDescriptor));

        ComponentPropertyDescriptor objectPropertyDescriptor =
                new ComponentPropertyDescriptor("configuration", typeObjectDescriptor, "Configuration");

        // When
        DefaultDescriptorDataValuesFiller
                .fill(testDataHolder, singletonList(objectPropertyDescriptor));

        // Then
        TypeObjectDescriptor.TypeObject configuration = testDataHolder.get("configuration");

        String defaultName = configuration.get("name");
        assertThat(defaultName).isEqualTo("Test name");

        int defaultZipCode = configuration.get("zipCode");
        assertThat(defaultZipCode).isEqualTo(23411);
    }

    @Test
    void shouldCorrectlyFillDefaultValuesForNestedComplexObjects() {
        // Given
        String object1FullyQualifiedName = "com.esb.test.Component1";
        String object2FullyQualifiedName = "com.esb.test.Component2";

        TypeObjectDescriptor typeObject2 =
                new TypeObjectDescriptor(object2FullyQualifiedName, false, asList(surnamePropertyDescriptor, zipCodePropertyDescriptor));
        ComponentPropertyDescriptor object2PropertyDescriptor =
                new ComponentPropertyDescriptor("configuration2", typeObject2, "Configuration 2");

        TypeObjectDescriptor typeObject1 =
                new TypeObjectDescriptor(object1FullyQualifiedName, false, asList(namePropertyDescriptor, object2PropertyDescriptor));
        ComponentPropertyDescriptor object1PropertyDescriptor =
                new ComponentPropertyDescriptor("configuration1", typeObject1, "Configuration 1");

        // When
        DefaultDescriptorDataValuesFiller
                .fill(testDataHolder, singletonList(object1PropertyDescriptor));

        // Then
        TypeObjectDescriptor.TypeObject configuration1 = testDataHolder.get("configuration1");
        String defaultName = configuration1.get("name");
        assertThat(defaultName).isEqualTo("Test name");

        TypeObjectDescriptor.TypeObject configuration2 = configuration1.get("configuration2");
        String defaultSurname = configuration2.get("surname");
        assertThat(defaultSurname).isEqualTo("Test surname");
    }

    @Test
    void shouldCorrectlyInitializeShareableComplexObjectWithDefaultReference() {
        // Given
        String objectFullyQualifiedName = "com.esb.test.Component";

        TypeObjectDescriptor typeObjectDescriptor =
                new TypeObjectDescriptor(objectFullyQualifiedName, true, asList(namePropertyDescriptor, zipCodePropertyDescriptor));

        ComponentPropertyDescriptor objectPropertyDescriptor =
                new ComponentPropertyDescriptor("configuration", typeObjectDescriptor, "Configuration");

        // When
        DefaultDescriptorDataValuesFiller
                .fill(testDataHolder, singletonList(objectPropertyDescriptor));

        // Then
        TypeObjectDescriptor.TypeObject configuration = testDataHolder.get("configuration");

        assertThat(configuration.keys()).containsExactly(Component.configRef());

        String configReference = configuration.get(Component.configRef());
        assertThat(configReference).isEqualTo(TypeObjectDescriptor.TypeObject.DEFAULT_CONFIG_REF);
    }

    private final TypePrimitiveDescriptor stringTypeDescriptor =
            new TypePrimitiveDescriptor(String.class);

    private final TypePrimitiveDescriptor intTypeDescriptor =
            new TypePrimitiveDescriptor(int.class);

    private final ComponentPropertyDescriptor namePropertyDescriptor =
            new ComponentPropertyDescriptor("name", stringTypeDescriptor, "Your name", "Test name");

    private final ComponentPropertyDescriptor surnamePropertyDescriptor =
            new ComponentPropertyDescriptor("surname", stringTypeDescriptor, "Your surname", "Test surname");

    private final ComponentPropertyDescriptor zipCodePropertyDescriptor =
            new ComponentPropertyDescriptor("zipCode", intTypeDescriptor, "ZIP Code", 23411);

}