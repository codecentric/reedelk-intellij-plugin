package com.reedelk.plugin.component.type.generic;

import com.google.common.collect.ImmutableMap;
import com.reedelk.plugin.component.domain.*;
import com.reedelk.plugin.fixture.ComponentNode2;
import com.reedelk.runtime.api.script.dynamicvalue.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;

import static com.reedelk.plugin.component.type.generic.SamplePropertyDescriptors.Primitives.*;
import static java.util.Arrays.asList;

public class SamplePropertyDescriptors {

    public static class Primitives {

        public static final TypeDescriptor integerTypeDescriptor = new TypePrimitiveDescriptor(int.class);
        public static final TypeDescriptor booleanTypeDescriptor = new TypePrimitiveDescriptor(boolean.class);
        public static final TypeDescriptor stringTypeDescriptor = new TypePrimitiveDescriptor(String.class);

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

        static final TypeDescriptor integerObjectTypeDescriptor = new TypePrimitiveDescriptor(Integer.class);
        static final TypeDescriptor longTypeDescriptor = new TypePrimitiveDescriptor(long.class);
        static final TypeDescriptor longObjectTypeDescriptor = new TypePrimitiveDescriptor(Long.class);
        static final TypeDescriptor floatTypeDescriptor = new TypePrimitiveDescriptor(float.class);
        static final TypeDescriptor floatObjectTypeDescriptor = new TypePrimitiveDescriptor(Float.class);
        static final TypeDescriptor doubleTypeDescriptor = new TypePrimitiveDescriptor(double.class);
        static final TypeDescriptor doubleObjectTypeDescriptor = new TypePrimitiveDescriptor(Double.class);
        static final TypeDescriptor booleanObjectTypeDescriptor = new TypePrimitiveDescriptor(Boolean.class);
        static final TypeDescriptor bigIntegerTypeDescriptor = new TypePrimitiveDescriptor(BigInteger.class);
        static final TypeDescriptor bigDecimalTypeDescriptor = new TypePrimitiveDescriptor(BigDecimal.class);

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

        static final TypeDescriptor dynamicBigDecimalTypeDescriptor = new TypeDynamicValueDescriptor<>(DynamicBigDecimal.class);
        static final TypeDescriptor dynamicBigIntegerTypeDescriptor = new TypeDynamicValueDescriptor<>(DynamicBigInteger.class);
        static final TypeDescriptor dynamicBooleanTypeDescriptor = new TypeDynamicValueDescriptor<>(DynamicBoolean.class);
        static final TypeDescriptor dynamicByteArrayTypeDescriptor = new TypeDynamicValueDescriptor<>(DynamicByteArray.class);
        static final TypeDescriptor dynamicDoubleTypeDescriptor = new TypeDynamicValueDescriptor<>(DynamicDouble.class);
        static final TypeDescriptor dynamicFloatTypeDescriptor = new TypeDynamicValueDescriptor<>(DynamicFloat.class);
        static final TypeDescriptor dynamicIntegerTypeDescriptor = new TypeDynamicValueDescriptor<>(DynamicInteger.class);
        static final TypeDescriptor dynamicLongTypeDescriptor = new TypeDynamicValueDescriptor<>(DynamicLong.class);
        static final TypeDescriptor dynamicObjectTypeDescriptor = new TypeDynamicValueDescriptor<>(DynamicObject.class);
        static final TypeDescriptor dynamicStringTypeDescriptor = new TypeDynamicValueDescriptor<>(DynamicString.class);

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

        static final TypeDescriptor enumTypeDescriptor = new TypeEnumDescriptor(valueAndDisplayMap, "NONE");
        static final TypeDescriptor mapTypeDescriptor = new TypeMapDescriptor("Headers");
        static final TypeDescriptor scriptTypeDescriptor = new TypeScriptDescriptor();
        static final TypeDescriptor comboTypeDescriptor = new TypeComboDescriptor(true, new String[]{"one", "two", "three"}, null);
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
                new TypeObjectDescriptor(ComponentNode2.class.getName(), asList(stringProperty, integerObjectProperty), Shared.NO, Collapsible.NO);

        static final TypeObjectDescriptor typeObjectSharedDescriptor =
                new TypeObjectDescriptor(ComponentNode2.class.getName(), asList(doubleObjectProperty, bigIntegerProperty), Shared.YES, Collapsible.NO);

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
