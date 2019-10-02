package com.reedelk.plugin.component.type.generic;

import com.google.common.collect.ImmutableMap;
import com.reedelk.plugin.component.domain.*;
import com.reedelk.plugin.fixture.ComponentNode2;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;

import static com.reedelk.plugin.component.type.generic.SamplePropertyDescriptors.Primitives.*;
import static java.util.Arrays.asList;

public class SamplePropertyDescriptors {

    public static class Primitives {
        public static final TypeDescriptor integerTypeDescriptor = new TypePrimitiveDescriptor(int.class);
        public static final TypeDescriptor integerObjectTypeDescriptor = new TypePrimitiveDescriptor(Integer.class);
        public static final TypeDescriptor longTypeDescriptor = new TypePrimitiveDescriptor(long.class);
        public static final TypeDescriptor longObjectTypeDescriptor = new TypePrimitiveDescriptor(Long.class);
        public static final TypeDescriptor floatTypeDescriptor = new TypePrimitiveDescriptor(float.class);
        public static final TypeDescriptor floatObjectTypeDescriptor = new TypePrimitiveDescriptor(Float.class);
        public static final TypeDescriptor doubleTypeDescriptor = new TypePrimitiveDescriptor(double.class);
        public static final TypeDescriptor doubleObjectTypeDescriptor = new TypePrimitiveDescriptor(Double.class);
        public static final TypeDescriptor booleanTypeDescriptor = new TypePrimitiveDescriptor(boolean.class);
        public static final TypeDescriptor booleanObjectTypeDescriptor = new TypePrimitiveDescriptor(Boolean.class);
        public static final TypeDescriptor stringTypeDescriptor = new TypePrimitiveDescriptor(String.class);
        public static final TypeDescriptor bigIntegerTypeDescriptor = new TypePrimitiveDescriptor(BigInteger.class);
        public static final TypeDescriptor bigDecimalTypeDescriptor = new TypePrimitiveDescriptor(BigDecimal.class);

        public static final ComponentPropertyDescriptor integerProperty =
                ComponentPropertyDescriptor.builder()
                        .type(integerTypeDescriptor)
                        .propertyName("integerProperty")
                        .displayName("Integer property")
                        .build();

        public static final ComponentPropertyDescriptor integerObjectProperty =
                ComponentPropertyDescriptor.builder()
                        .type(integerObjectTypeDescriptor)
                        .propertyName("integerObjectProperty")
                        .displayName("Integer object property")
                        .build();

        public static final ComponentPropertyDescriptor longProperty =
                ComponentPropertyDescriptor.builder()
                        .type(longTypeDescriptor)
                        .propertyName("longProperty")
                        .displayName("Long property")
                        .build();

        public static final ComponentPropertyDescriptor longObjectProperty =
                ComponentPropertyDescriptor.builder()
                        .type(longObjectTypeDescriptor)
                        .propertyName("longObjectProperty")
                        .displayName("Long object property")
                        .build();

        public static final ComponentPropertyDescriptor floatProperty =
                ComponentPropertyDescriptor.builder()
                        .type(floatTypeDescriptor)
                        .propertyName("floatProperty")
                        .displayName("Float property")
                        .build();

        public static final ComponentPropertyDescriptor floatObjectProperty =
                ComponentPropertyDescriptor.builder()
                        .type(floatObjectTypeDescriptor)
                        .propertyName("floatObjectProperty")
                        .displayName("Float object property")
                        .build();

        public static final ComponentPropertyDescriptor doubleProperty =
                ComponentPropertyDescriptor.builder()
                        .type(doubleTypeDescriptor)
                        .propertyName("doubleProperty")
                        .displayName("Double property")
                        .build();

        public static final ComponentPropertyDescriptor doubleObjectProperty =
                ComponentPropertyDescriptor.builder()
                        .type(doubleObjectTypeDescriptor)
                        .propertyName("doubleObjectProperty")
                        .displayName("Double object property")
                        .build();

        public static final ComponentPropertyDescriptor booleanProperty =
                ComponentPropertyDescriptor.builder()
                        .type(booleanTypeDescriptor)
                        .propertyName("booleanProperty")
                        .displayName("Boolean property")
                        .build();

        public static final ComponentPropertyDescriptor booleanObjectProperty =
                ComponentPropertyDescriptor.builder()
                        .type(booleanObjectTypeDescriptor)
                        .propertyName("booleanObjectProperty")
                        .displayName("Boolean object property")
                        .build();

        public static final ComponentPropertyDescriptor stringProperty =
                ComponentPropertyDescriptor.builder()
                        .type(stringTypeDescriptor)
                        .propertyName("stringProperty")
                        .displayName("String property")
                        .defaultValue("")
                        .build();

        public static final ComponentPropertyDescriptor bigIntegerProperty =
                ComponentPropertyDescriptor.builder()
                        .type(bigIntegerTypeDescriptor)
                        .propertyName("bigIntegerProperty")
                        .displayName("Big Integer property")
                        .build();

        public static final ComponentPropertyDescriptor bigDecimalProperty =
                ComponentPropertyDescriptor.builder()
                        .type(bigDecimalTypeDescriptor)
                        .propertyName("bigDecimalProperty")
                        .displayName("Big Decimal property")
                        .build();
    }

    static class DynamicTypes {

    }

    static class DynamicMapTypes {

    }

    /**
     * tmp.put(Enum.class, new EnumConverter());
     * <p>
     * tmp.put(TypeFileDescriptor.TypeFile .class, new FileConverter());
     * tmp.put(TypeComboDescriptor.TypeCombo .class, new ComboConverter());
     */

    static class SpecialTypes {

        private static final Map<String, String> valueAndDisplayMap = ImmutableMap.of("NONE", "No config", "CERT", "Certificate");
        public static final TypeDescriptor enumTypeDescriptor = new TypeEnumDescriptor(valueAndDisplayMap, "NONE");

        public static final ComponentPropertyDescriptor enumProperty =
                ComponentPropertyDescriptor.builder()
                        .type(enumTypeDescriptor)
                        .propertyName("enumProperty")
                        .displayName("Enum property")
                        .build();

        public static final TypeDescriptor mapTypeDescriptor = new TypeMapDescriptor("Headers");

        public static final ComponentPropertyDescriptor mapProperty =
                ComponentPropertyDescriptor.builder()
                        .type(mapTypeDescriptor)
                        .propertyName("mapProperty")
                        .displayName("Map property")
                        .build();

        public static final TypeDescriptor scriptTypeDescriptor = new TypeScriptDescriptor();

        public static final ComponentPropertyDescriptor scriptProperty =
                ComponentPropertyDescriptor.builder()
                        .type(scriptTypeDescriptor)
                        .propertyName("scriptProperty")
                        .displayName("Script property")
                        .build();

        public static final TypeDescriptor comboTypeDescriptor = new TypeComboDescriptor(true, new String[]{"one", "two", "three"});

        public static final ComponentPropertyDescriptor comboProperty =
                ComponentPropertyDescriptor.builder()
                        .type(comboTypeDescriptor)
                        .propertyName("comboProperty")
                        .displayName("Combo property")
                        .build();

        public static final TypeDescriptor fileTypeDescriptor = new TypeFileDescriptor();

        public static final ComponentPropertyDescriptor fileProperty =
                ComponentPropertyDescriptor.builder()
                        .type(fileTypeDescriptor)
                        .propertyName("fileProperty")
                        .displayName("File property")
                        .build();

    }

    static class TypeObjects {

        public static final TypeObjectDescriptor typeObjectDescriptor =
                new TypeObjectDescriptor(ComponentNode2.class.getName(), asList(stringProperty, integerObjectProperty), Shared.NO, Collapsible.NO);

        public static final TypeObjectDescriptor typeObjectSharedDescriptor =
                new TypeObjectDescriptor(ComponentNode2.class.getName(), asList(doubleObjectProperty, bigIntegerProperty), Shared.YES, Collapsible.NO);

        public static final ComponentPropertyDescriptor typeObjectProperty =
                ComponentPropertyDescriptor.builder()
                        .type(typeObjectDescriptor)
                        .propertyName("typeObjectProperty")
                        .displayName("Type Object property")
                        .defaultValue(null)
                        .build();

        public static final ComponentPropertyDescriptor typeObjectSharedProperty =
                ComponentPropertyDescriptor.builder()
                        .type(typeObjectSharedDescriptor)
                        .propertyName("typeObjectSharedProperty")
                        .displayName("Type Object shared property")
                        .defaultValue(null)
                        .build();

    }
}
