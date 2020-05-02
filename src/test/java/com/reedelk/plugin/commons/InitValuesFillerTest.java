package com.reedelk.plugin.commons;

import com.reedelk.module.descriptor.model.component.ComponentDataHolder;
import com.reedelk.module.descriptor.model.property.ObjectDescriptor;
import com.reedelk.module.descriptor.model.property.PropertyDescriptor;
import com.reedelk.module.descriptor.model.property.Shared;
import com.reedelk.plugin.service.module.impl.configuration.ConfigMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.reedelk.plugin.component.type.generic.SamplePropertyDescriptors.Primitives;
import static com.reedelk.plugin.testutils.ObjectFactories.createTypeObjectDescriptor;
import static com.reedelk.runtime.commons.JsonParser.Component;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

class InitValuesFillerTest {

    private ComponentDataHolder testDataHolder;

    @BeforeEach
    void setUp() {
        testDataHolder = new ConfigMetadata(TypeObjectFactory.newInstance());
    }

    @Test
    void shouldCorrectlyFillInitValuesForPrimitiveTypes() {
        // When
        InitValuesFiller
                .fill(testDataHolder, asList(namePropertyDescriptor, surnamePropertyDescriptor));

        // Then
        assertThat(testDataHolder.keys()).containsExactlyInAnyOrder("name", "surname");

        String defaultName = testDataHolder.get("name");
        assertThat(defaultName).isEqualTo("Test name");

        String defaultSurname = testDataHolder.get("surname");
        assertThat(defaultSurname).isEqualTo("Test surname");
    }

    @Test
    void shouldCorrectlyFillNullDefaultValueForUndefinedInitValue() {
        // Given

        PropertyDescriptor addressPropertyDescriptorWithoutInitValue =
                PropertyDescriptor.builder()
                        .name("address")
                        .displayName("Address")
                        .type(Primitives.stringTypeDescriptor)
                        .build();

        // When
        InitValuesFiller
                .fill(testDataHolder, singletonList(addressPropertyDescriptorWithoutInitValue));

        // Then
        String defaultAddress = testDataHolder.get("address");
        assertThat(defaultAddress).isNull();
    }

    @Test
    void shouldCorrectlyFillInitValuesForComplexTypes() {
        // Given
        String objectFullyQualifiedName = "com.esb.test.Component";

        ObjectDescriptor typeObjectDescriptor =
                createTypeObjectDescriptor(objectFullyQualifiedName, asList(namePropertyDescriptor, zipCodePropertyDescriptor));

        PropertyDescriptor objectPropertyDescriptor =
                PropertyDescriptor.builder()
                        .name("configuration")
                        .type(typeObjectDescriptor)
                        .displayName("Configuration")
                        .build();

        // When
        InitValuesFiller
                .fill(testDataHolder, singletonList(objectPropertyDescriptor));

        // Then
        ObjectDescriptor.TypeObject configuration = testDataHolder.get("configuration");

        String defaultName = configuration.get("name");
        assertThat(defaultName).isEqualTo("Test name");

        int defaultZipCode = configuration.get("zipCode");
        assertThat(defaultZipCode).isEqualTo(23411);
    }

    @Test
    void shouldCorrectlyFillInitValuesForNestedComplexObjects() {
        // Given
        String object1FullyQualifiedName = "com.esb.test.Component1";
        String object2FullyQualifiedName = "com.esb.test.Component2";

        ObjectDescriptor typeObject2 = createTypeObjectDescriptor(object2FullyQualifiedName, asList(surnamePropertyDescriptor, zipCodePropertyDescriptor));
        PropertyDescriptor object2PropertyDescriptor =
                PropertyDescriptor.builder()
                        .name("configuration2")
                        .type(typeObject2)
                        .displayName("Configuration 2")
                        .build();

        ObjectDescriptor typeObject1 =
                createTypeObjectDescriptor(object1FullyQualifiedName, asList(namePropertyDescriptor, object2PropertyDescriptor));
        PropertyDescriptor object1PropertyDescriptor =
                PropertyDescriptor.builder()
                        .name("configuration1")
                        .type(typeObject1)
                        .displayName("Configuration 1")
                        .build();

        // When
        InitValuesFiller
                .fill(testDataHolder, singletonList(object1PropertyDescriptor));

        // Then
        ObjectDescriptor.TypeObject configuration1 = testDataHolder.get("configuration1");
        String defaultName = configuration1.get("name");
        assertThat(defaultName).isEqualTo("Test name");

        ObjectDescriptor.TypeObject configuration2 = configuration1.get("configuration2");
        String defaultSurname = configuration2.get("surname");
        assertThat(defaultSurname).isEqualTo("Test surname");
    }

    @Test
    void shouldCorrectlyInitializeShareableComplexObjectWithDefaultReference() {
        // Given
        String objectFullyQualifiedName = "com.esb.test.Component";

        ObjectDescriptor typeObjectDescriptor =
                createTypeObjectDescriptor(objectFullyQualifiedName, asList(namePropertyDescriptor, zipCodePropertyDescriptor), Shared.YES);

        PropertyDescriptor objectPropertyDescriptor =
                PropertyDescriptor.builder()
                        .name("configuration")
                        .displayName("Configuration")
                        .type(typeObjectDescriptor)
                        .build();

        // When
        InitValuesFiller
                .fill(testDataHolder, singletonList(objectPropertyDescriptor));

        // Then
        ObjectDescriptor.TypeObject configuration = testDataHolder.get("configuration");

        assertThat(configuration.keys()).containsExactly(Component.ref());

        String reference = configuration.get(Component.ref());
        assertThat(reference).isEqualTo(ObjectDescriptor.TypeObject.DEFAULT_CONFIG_REF);
    }


    private final PropertyDescriptor namePropertyDescriptor =
            PropertyDescriptor.builder()
                    .name("name")
                    .type(Primitives.stringTypeDescriptor)
                    .displayName("Your name")
                    .initValue("Test name")
                    .build();

    private final PropertyDescriptor surnamePropertyDescriptor =
            PropertyDescriptor.builder()
                    .name("surname")
                    .type(Primitives.stringTypeDescriptor)
                    .displayName("Your surname")
                    .initValue("Test surname")
                    .build();

    private final PropertyDescriptor zipCodePropertyDescriptor =
            PropertyDescriptor.builder()
                    .name("zipCode")
                    .type(Primitives.integerTypeDescriptor)
                    .displayName("ZIP Code")
                    .initValue("23411")
                    .build();
}
