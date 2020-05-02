package com.reedelk.plugin.component.type.generic;

import com.google.common.collect.ImmutableMap;
import com.reedelk.module.descriptor.model.property.*;
import com.reedelk.plugin.fixture.ComponentNode2;
import com.reedelk.runtime.api.script.dynamicvalue.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;

import static com.reedelk.plugin.component.type.generic.SamplePropertyDescriptors.Primitives.*;
import static com.reedelk.plugin.testutils.ObjectFactories.*;
import static java.util.Arrays.asList;

public class SamplePropertyDescriptors {

    public static class Primitives {

        public static final PropertyTypeDescriptor integerTypeDescriptor = createTypePrimitiveDescriptor(int.class);
        public static final PropertyTypeDescriptor booleanTypeDescriptor = createTypePrimitiveDescriptor(boolean.class);
        public static final PropertyTypeDescriptor stringTypeDescriptor = createTypePrimitiveDescriptor(String.class);

        static final PropertyDescriptor integerProperty =
                PropertyDescriptor.builder()
                        .type(integerTypeDescriptor)
                        .name("integerProperty")
                        .displayName("Integer property")
                        .build();

        public static final PropertyDescriptor booleanProperty =
                PropertyDescriptor.builder()
                        .type(booleanTypeDescriptor)
                        .name("booleanProperty")
                        .displayName("Boolean property")
                        .build();

        public static final PropertyDescriptor stringProperty =
                PropertyDescriptor.builder()
                        .type(stringTypeDescriptor)
                        .name("stringProperty")
                        .displayName("String property")
                        .initValue("")
                        .build();

        static final PropertyTypeDescriptor integerObjectTypeDescriptor = createTypePrimitiveDescriptor(Integer.class);
        static final PropertyTypeDescriptor longTypeDescriptor = createTypePrimitiveDescriptor(long.class);
        static final PropertyTypeDescriptor longObjectTypeDescriptor = createTypePrimitiveDescriptor(Long.class);
        static final PropertyTypeDescriptor floatTypeDescriptor = createTypePrimitiveDescriptor(float.class);
        static final PropertyTypeDescriptor floatObjectTypeDescriptor = createTypePrimitiveDescriptor(Float.class);
        static final PropertyTypeDescriptor doubleTypeDescriptor = createTypePrimitiveDescriptor(double.class);
        static final PropertyTypeDescriptor doubleObjectTypeDescriptor = createTypePrimitiveDescriptor(Double.class);
        static final PropertyTypeDescriptor booleanObjectTypeDescriptor = createTypePrimitiveDescriptor(Boolean.class);
        static final PropertyTypeDescriptor bigIntegerTypeDescriptor = createTypePrimitiveDescriptor(BigInteger.class);
        static final PropertyTypeDescriptor bigDecimalTypeDescriptor = createTypePrimitiveDescriptor(BigDecimal.class);

        static final PropertyDescriptor integerObjectProperty =
                PropertyDescriptor.builder()
                        .type(integerObjectTypeDescriptor)
                        .name("integerObjectProperty")
                        .displayName("Integer object property")
                        .build();

        static final PropertyDescriptor longProperty =
                PropertyDescriptor.builder()
                        .type(longTypeDescriptor)
                        .name("longProperty")
                        .displayName("Long property")
                        .build();

        static final PropertyDescriptor longObjectProperty =
                PropertyDescriptor.builder()
                        .type(longObjectTypeDescriptor)
                        .name("longObjectProperty")
                        .displayName("Long object property")
                        .build();

        static final PropertyDescriptor floatProperty =
                PropertyDescriptor.builder()
                        .type(floatTypeDescriptor)
                        .name("floatProperty")
                        .displayName("Float property")
                        .build();

        static final PropertyDescriptor floatObjectProperty =
                PropertyDescriptor.builder()
                        .type(floatObjectTypeDescriptor)
                        .name("floatObjectProperty")
                        .displayName("Float object property")
                        .build();

        static final PropertyDescriptor doubleProperty =
                PropertyDescriptor.builder()
                        .type(doubleTypeDescriptor)
                        .name("doubleProperty")
                        .displayName("Double property")
                        .build();

        static final PropertyDescriptor doubleObjectProperty =
                PropertyDescriptor.builder()
                        .type(doubleObjectTypeDescriptor)
                        .name("doubleObjectProperty")
                        .displayName("Double object property")
                        .build();

        static final PropertyDescriptor booleanObjectProperty =
                PropertyDescriptor.builder()
                        .type(booleanObjectTypeDescriptor)
                        .name("booleanObjectProperty")
                        .displayName("Boolean object property")
                        .build();

        static final PropertyDescriptor bigIntegerProperty =
                PropertyDescriptor.builder()
                        .type(bigIntegerTypeDescriptor)
                        .name("bigIntegerProperty")
                        .displayName("Big Integer property")
                        .build();

        static final PropertyDescriptor bigDecimalProperty =
                PropertyDescriptor.builder()
                        .type(bigDecimalTypeDescriptor)
                        .name("bigDecimalProperty")
                        .displayName("Big Decimal property")
                        .build();
    }

    static class DynamicTypes {

        static final PropertyTypeDescriptor dynamicBigDecimalTypeDescriptor = createTypeDynamicValueDescriptor(DynamicBigDecimal.class);
        static final PropertyTypeDescriptor dynamicBigIntegerTypeDescriptor = createTypeDynamicValueDescriptor(DynamicBigInteger.class);
        static final PropertyTypeDescriptor dynamicBooleanTypeDescriptor = createTypeDynamicValueDescriptor(DynamicBoolean.class);
        static final PropertyTypeDescriptor dynamicByteArrayTypeDescriptor = createTypeDynamicValueDescriptor(DynamicByteArray.class);
        static final PropertyTypeDescriptor dynamicDoubleTypeDescriptor = createTypeDynamicValueDescriptor(DynamicDouble.class);
        static final PropertyTypeDescriptor dynamicFloatTypeDescriptor = createTypeDynamicValueDescriptor(DynamicFloat.class);
        static final PropertyTypeDescriptor dynamicIntegerTypeDescriptor = createTypeDynamicValueDescriptor(DynamicInteger.class);
        static final PropertyTypeDescriptor dynamicLongTypeDescriptor = createTypeDynamicValueDescriptor(DynamicLong.class);
        static final PropertyTypeDescriptor dynamicObjectTypeDescriptor = createTypeDynamicValueDescriptor(DynamicObject.class);
        static final PropertyTypeDescriptor dynamicStringTypeDescriptor = createTypeDynamicValueDescriptor(DynamicString.class);

        static final PropertyDescriptor dynamicBigDecimalProperty =
                PropertyDescriptor.builder()
                        .type(dynamicBigDecimalTypeDescriptor)
                        .name("dynamicBigDecimalProperty")
                        .displayName("Dynamic Big Decimal property")
                        .build();

        static final PropertyDescriptor dynamicBigIntegerProperty =
                PropertyDescriptor.builder()
                        .type(dynamicBigIntegerTypeDescriptor)
                        .name("dynamicBigIntegerProperty")
                        .displayName("Dynamic Big Integer property")
                        .build();

        static final PropertyDescriptor dynamicBooleanProperty =
                PropertyDescriptor.builder()
                        .type(dynamicBooleanTypeDescriptor)
                        .name("dynamicBooleanProperty")
                        .displayName("Dynamic Boolean property")
                        .build();

        static final PropertyDescriptor dynamicByteArrayProperty =
                PropertyDescriptor.builder()
                        .type(dynamicByteArrayTypeDescriptor)
                        .name("dynamicByteArrayProperty")
                        .displayName("Dynamic Byte Array property")
                        .build();

        static final PropertyDescriptor dynamicDoubleProperty =
                PropertyDescriptor.builder()
                        .type(dynamicDoubleTypeDescriptor)
                        .name("dynamicDoubleProperty")
                        .displayName("Dynamic Double property")
                        .build();

        static final PropertyDescriptor dynamicFloatProperty =
                PropertyDescriptor.builder()
                        .type(dynamicFloatTypeDescriptor)
                        .name("dynamicFloatProperty")
                        .displayName("Dynamic Float property")
                        .build();

        static final PropertyDescriptor dynamicIntegerProperty =
                PropertyDescriptor.builder()
                        .type(dynamicIntegerTypeDescriptor)
                        .name("dynamicIntegerProperty")
                        .displayName("Dynamic Integer property")
                        .build();

        static final PropertyDescriptor dynamicLongProperty =
                PropertyDescriptor.builder()
                        .type(dynamicLongTypeDescriptor)
                        .name("dynamicLongProperty")
                        .displayName("Dynamic Long property")
                        .build();

        static final PropertyDescriptor dynamicObjectProperty =
                PropertyDescriptor.builder()
                        .type(dynamicObjectTypeDescriptor)
                        .name("dynamicObjectProperty")
                        .displayName("Dynamic Object property")
                        .build();

        static final PropertyDescriptor dynamicStringProperty =
                PropertyDescriptor.builder()
                        .type(dynamicStringTypeDescriptor)
                        .name("dynamicStringProperty")
                        .displayName("Dynamic String property")
                        .build();
    }

    static class SpecialTypes {

        private static final Map<String, String> valueAndDisplayMap = ImmutableMap.of("NONE", "No config", "CERT", "Certificate");

        static final PropertyTypeDescriptor enumTypeDescriptor = createTypeEnumDescriptor(valueAndDisplayMap);
        static final PropertyTypeDescriptor mapTypeDescriptor = createTypeMapDescriptor("Headers", stringTypeDescriptor);
        static final PropertyTypeDescriptor listTypeDescriptor = createTypeListDescriptor("Tags", stringTypeDescriptor);
        static final PropertyTypeDescriptor mapTypeDescriptorWithCustomValueType = createTypeMapDescriptor("Responses", TypeObjects.typeObjectDescriptor);
        static final PropertyTypeDescriptor listTypeDescriptorWithCustomValueType = createTypeListDescriptor("Responses", TypeObjects.typeObjectDescriptor);
        static final PropertyTypeDescriptor scriptTypeDescriptor = new ScriptDescriptor();
        static final PropertyTypeDescriptor comboTypeDescriptor = createTypeComboDescriptor(true, new String[]{"one", "two", "three"}, null);
        static final PropertyTypeDescriptor resourceTypeDescriptor = new ResourceTextDescriptor();

        static final PropertyDescriptor enumProperty =
                PropertyDescriptor.builder()
                        .type(enumTypeDescriptor)
                        .name("enumProperty")
                        .displayName("Enum property")
                        .build();

        static final PropertyDescriptor mapProperty =
                PropertyDescriptor.builder()
                        .type(mapTypeDescriptor)
                        .name("mapProperty")
                        .displayName("Map property")
                        .build();

        static final PropertyDescriptor listProperty =
                PropertyDescriptor.builder()
                        .type(listTypeDescriptor)
                        .name("listProperty")
                        .displayName("List property")
                        .build();

        static final PropertyDescriptor mapPropertyWithCustomValueType =
                PropertyDescriptor.builder()
                        .type(mapTypeDescriptorWithCustomValueType)
                        .name("mapPropertyWithCustomValueType")
                        .displayName("Map property with custom value type")
                        .build();

        static final PropertyDescriptor listPropertyWithCustomValueType =
                PropertyDescriptor.builder()
                        .type(listTypeDescriptorWithCustomValueType)
                        .name("listPropertyWithCustomValueType")
                        .displayName("List property with custom value type")
                        .build();

        static final PropertyDescriptor scriptProperty =
                PropertyDescriptor.builder()
                        .type(scriptTypeDescriptor)
                        .name("scriptProperty")
                        .displayName("Script property")
                        .build();

        static final PropertyDescriptor comboProperty =
                PropertyDescriptor.builder()
                        .type(comboTypeDescriptor)
                        .name("comboProperty")
                        .displayName("Combo property")
                        .build();

        static final PropertyDescriptor resourceProperty =
                PropertyDescriptor.builder()
                        .type(resourceTypeDescriptor)
                        .name("resourceProperty")
                        .displayName("Resource property")
                        .build();
    }

    static class TypeObjects {

        static final ObjectDescriptor typeObjectDescriptor =
                createTypeObjectDescriptor(ComponentNode2.class.getName(), asList(stringProperty, integerObjectProperty));

        static final ObjectDescriptor typeObjectSharedDescriptor =
                createTypeObjectDescriptor(ComponentNode2.class.getName(), asList(doubleObjectProperty, bigIntegerProperty), Shared.YES);

        static final PropertyDescriptor typeObjectProperty =
                PropertyDescriptor.builder()
                        .type(typeObjectDescriptor)
                        .name("typeObjectProperty")
                        .displayName("Type Object property")
                        .initValue(null)
                        .build();

        static final PropertyDescriptor typeObjectSharedProperty =
                PropertyDescriptor.builder()
                        .type(typeObjectSharedDescriptor)
                        .name("typeObjectSharedProperty")
                        .displayName("Type Object shared property")
                        .initValue(null)
                        .build();

    }
}
