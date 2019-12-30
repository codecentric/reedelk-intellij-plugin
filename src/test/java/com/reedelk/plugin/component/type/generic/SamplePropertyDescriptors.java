package com.reedelk.plugin.component.type.generic;

import com.google.common.collect.ImmutableMap;
import com.reedelk.module.descriptor.model.*;
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

        public static final TypeDescriptor integerTypeDescriptor = createTypePrimitiveDescriptor(int.class);
        public static final TypeDescriptor booleanTypeDescriptor = createTypePrimitiveDescriptor(boolean.class);
        public static final TypeDescriptor stringTypeDescriptor = createTypePrimitiveDescriptor(String.class);

        static final ComponentPropertyDescriptor integerProperty =
                ComponentPropertyDescriptor.builder()
                        .type(integerTypeDescriptor)
                        .propertyName("integerProperty")
                        .displayName("Integer property")
                        .build();

        public static final ComponentPropertyDescriptor booleanProperty =
                ComponentPropertyDescriptor.builder()
                        .type(booleanTypeDescriptor)
                        .propertyName("booleanProperty")
                        .displayName("Boolean property")
                        .build();

        public static final ComponentPropertyDescriptor stringProperty =
                ComponentPropertyDescriptor.builder()
                        .type(stringTypeDescriptor)
                        .propertyName("stringProperty")
                        .displayName("String property")
                        .defaultValue("")
                        .build();

        static final TypeDescriptor integerObjectTypeDescriptor = createTypePrimitiveDescriptor(Integer.class);
        static final TypeDescriptor longTypeDescriptor = createTypePrimitiveDescriptor(long.class);
        static final TypeDescriptor longObjectTypeDescriptor = createTypePrimitiveDescriptor(Long.class);
        static final TypeDescriptor floatTypeDescriptor = createTypePrimitiveDescriptor(float.class);
        static final TypeDescriptor floatObjectTypeDescriptor = createTypePrimitiveDescriptor(Float.class);
        static final TypeDescriptor doubleTypeDescriptor = createTypePrimitiveDescriptor(double.class);
        static final TypeDescriptor doubleObjectTypeDescriptor = createTypePrimitiveDescriptor(Double.class);
        static final TypeDescriptor booleanObjectTypeDescriptor = createTypePrimitiveDescriptor(Boolean.class);
        static final TypeDescriptor bigIntegerTypeDescriptor = createTypePrimitiveDescriptor(BigInteger.class);
        static final TypeDescriptor bigDecimalTypeDescriptor = createTypePrimitiveDescriptor(BigDecimal.class);

        static final ComponentPropertyDescriptor integerObjectProperty =
                ComponentPropertyDescriptor.builder()
                        .type(integerObjectTypeDescriptor)
                        .propertyName("integerObjectProperty")
                        .displayName("Integer object property")
                        .build();

        static final ComponentPropertyDescriptor longProperty =
                ComponentPropertyDescriptor.builder()
                        .type(longTypeDescriptor)
                        .propertyName("longProperty")
                        .displayName("Long property")
                        .build();

        static final ComponentPropertyDescriptor longObjectProperty =
                ComponentPropertyDescriptor.builder()
                        .type(longObjectTypeDescriptor)
                        .propertyName("longObjectProperty")
                        .displayName("Long object property")
                        .build();

        static final ComponentPropertyDescriptor floatProperty =
                ComponentPropertyDescriptor.builder()
                        .type(floatTypeDescriptor)
                        .propertyName("floatProperty")
                        .displayName("Float property")
                        .build();

        static final ComponentPropertyDescriptor floatObjectProperty =
                ComponentPropertyDescriptor.builder()
                        .type(floatObjectTypeDescriptor)
                        .propertyName("floatObjectProperty")
                        .displayName("Float object property")
                        .build();

        static final ComponentPropertyDescriptor doubleProperty =
                ComponentPropertyDescriptor.builder()
                        .type(doubleTypeDescriptor)
                        .propertyName("doubleProperty")
                        .displayName("Double property")
                        .build();

        static final ComponentPropertyDescriptor doubleObjectProperty =
                ComponentPropertyDescriptor.builder()
                        .type(doubleObjectTypeDescriptor)
                        .propertyName("doubleObjectProperty")
                        .displayName("Double object property")
                        .build();

        static final ComponentPropertyDescriptor booleanObjectProperty =
                ComponentPropertyDescriptor.builder()
                        .type(booleanObjectTypeDescriptor)
                        .propertyName("booleanObjectProperty")
                        .displayName("Boolean object property")
                        .build();

        static final ComponentPropertyDescriptor bigIntegerProperty =
                ComponentPropertyDescriptor.builder()
                        .type(bigIntegerTypeDescriptor)
                        .propertyName("bigIntegerProperty")
                        .displayName("Big Integer property")
                        .build();

        static final ComponentPropertyDescriptor bigDecimalProperty =
                ComponentPropertyDescriptor.builder()
                        .type(bigDecimalTypeDescriptor)
                        .propertyName("bigDecimalProperty")
                        .displayName("Big Decimal property")
                        .build();
    }

    static class DynamicTypes {

        static final TypeDescriptor dynamicBigDecimalTypeDescriptor = createTypeDynamicValueDescriptor(DynamicBigDecimal.class);
        static final TypeDescriptor dynamicBigIntegerTypeDescriptor = createTypeDynamicValueDescriptor(DynamicBigInteger.class);
        static final TypeDescriptor dynamicBooleanTypeDescriptor = createTypeDynamicValueDescriptor(DynamicBoolean.class);
        static final TypeDescriptor dynamicByteArrayTypeDescriptor = createTypeDynamicValueDescriptor(DynamicByteArray.class);
        static final TypeDescriptor dynamicDoubleTypeDescriptor = createTypeDynamicValueDescriptor(DynamicDouble.class);
        static final TypeDescriptor dynamicFloatTypeDescriptor = createTypeDynamicValueDescriptor(DynamicFloat.class);
        static final TypeDescriptor dynamicIntegerTypeDescriptor = createTypeDynamicValueDescriptor(DynamicInteger.class);
        static final TypeDescriptor dynamicLongTypeDescriptor = createTypeDynamicValueDescriptor(DynamicLong.class);
        static final TypeDescriptor dynamicObjectTypeDescriptor = createTypeDynamicValueDescriptor(DynamicObject.class);
        static final TypeDescriptor dynamicStringTypeDescriptor = createTypeDynamicValueDescriptor(DynamicString.class);

        static final ComponentPropertyDescriptor dynamicBigDecimalProperty =
                ComponentPropertyDescriptor.builder()
                        .type(dynamicBigDecimalTypeDescriptor)
                        .propertyName("dynamicBigDecimalProperty")
                        .displayName("Dynamic Big Decimal property")
                        .build();

        static final ComponentPropertyDescriptor dynamicBigIntegerProperty =
                ComponentPropertyDescriptor.builder()
                        .type(dynamicBigIntegerTypeDescriptor)
                        .propertyName("dynamicBigIntegerProperty")
                        .displayName("Dynamic Big Integer property")
                        .build();

        static final ComponentPropertyDescriptor dynamicBooleanProperty =
                ComponentPropertyDescriptor.builder()
                        .type(dynamicBooleanTypeDescriptor)
                        .propertyName("dynamicBooleanProperty")
                        .displayName("Dynamic Boolean property")
                        .build();

        static final ComponentPropertyDescriptor dynamicByteArrayProperty =
                ComponentPropertyDescriptor.builder()
                        .type(dynamicByteArrayTypeDescriptor)
                        .propertyName("dynamicByteArrayProperty")
                        .displayName("Dynamic Byte Array property")
                        .build();

        static final ComponentPropertyDescriptor dynamicDoubleProperty =
                ComponentPropertyDescriptor.builder()
                        .type(dynamicDoubleTypeDescriptor)
                        .propertyName("dynamicDoubleProperty")
                        .displayName("Dynamic Double property")
                        .build();

        static final ComponentPropertyDescriptor dynamicFloatProperty =
                ComponentPropertyDescriptor.builder()
                        .type(dynamicFloatTypeDescriptor)
                        .propertyName("dynamicFloatProperty")
                        .displayName("Dynamic Float property")
                        .build();

        static final ComponentPropertyDescriptor dynamicIntegerProperty =
                ComponentPropertyDescriptor.builder()
                        .type(dynamicIntegerTypeDescriptor)
                        .propertyName("dynamicIntegerProperty")
                        .displayName("Dynamic Integer property")
                        .build();

        static final ComponentPropertyDescriptor dynamicLongProperty =
                ComponentPropertyDescriptor.builder()
                        .type(dynamicLongTypeDescriptor)
                        .propertyName("dynamicLongProperty")
                        .displayName("Dynamic Long property")
                        .build();

        static final ComponentPropertyDescriptor dynamicObjectProperty =
                ComponentPropertyDescriptor.builder()
                        .type(dynamicObjectTypeDescriptor)
                        .propertyName("dynamicObjectProperty")
                        .displayName("Dynamic Object property")
                        .build();

        static final ComponentPropertyDescriptor dynamicStringProperty =
                ComponentPropertyDescriptor.builder()
                        .type(dynamicStringTypeDescriptor)
                        .propertyName("dynamicStringProperty")
                        .displayName("Dynamic String property")
                        .build();
    }

    static class SpecialTypes {

        private static final Map<String, String> valueAndDisplayMap = ImmutableMap.of("NONE", "No config", "CERT", "Certificate");

        static final TypeDescriptor enumTypeDescriptor = createTypeEnumDescriptor(valueAndDisplayMap);
        static final TypeDescriptor mapTypeDescriptor = createTypeMapDescriptor("Headers");
        static final TypeDescriptor scriptTypeDescriptor = new TypeScriptDescriptor();
        static final TypeDescriptor comboTypeDescriptor = createTypeComboDescriptor(true, new String[]{"one", "two", "three"}, null);
        static final TypeDescriptor resourceTypeDescriptor = new TypeResourceTextDescriptor();

        static final ComponentPropertyDescriptor enumProperty =
                ComponentPropertyDescriptor.builder()
                        .type(enumTypeDescriptor)
                        .propertyName("enumProperty")
                        .displayName("Enum property")
                        .build();

        static final ComponentPropertyDescriptor mapProperty =
                ComponentPropertyDescriptor.builder()
                        .type(mapTypeDescriptor)
                        .propertyName("mapProperty")
                        .displayName("Map property")
                        .build();

        static final ComponentPropertyDescriptor scriptProperty =
                ComponentPropertyDescriptor.builder()
                        .type(scriptTypeDescriptor)
                        .propertyName("scriptProperty")
                        .displayName("Script property")
                        .build();

        static final ComponentPropertyDescriptor comboProperty =
                ComponentPropertyDescriptor.builder()
                        .type(comboTypeDescriptor)
                        .propertyName("comboProperty")
                        .displayName("Combo property")
                        .build();

        static final ComponentPropertyDescriptor resourceProperty =
                ComponentPropertyDescriptor.builder()
                        .type(resourceTypeDescriptor)
                        .propertyName("resourceProperty")
                        .displayName("Resource property")
                        .build();
    }

    static class TypeObjects {

        static final TypeObjectDescriptor typeObjectDescriptor =
                createTypeObjectDescriptor(ComponentNode2.class.getName(), asList(stringProperty, integerObjectProperty));

        static final TypeObjectDescriptor typeObjectSharedDescriptor =
                createTypeObjectDescriptor(ComponentNode2.class.getName(), asList(doubleObjectProperty, bigIntegerProperty), Shared.YES);

        static final ComponentPropertyDescriptor typeObjectProperty =
                ComponentPropertyDescriptor.builder()
                        .type(typeObjectDescriptor)
                        .propertyName("typeObjectProperty")
                        .displayName("Type Object property")
                        .defaultValue(null)
                        .build();

        static final ComponentPropertyDescriptor typeObjectSharedProperty =
                ComponentPropertyDescriptor.builder()
                        .type(typeObjectSharedDescriptor)
                        .propertyName("typeObjectSharedProperty")
                        .displayName("Type Object shared property")
                        .defaultValue(null)
                        .build();

    }
}
