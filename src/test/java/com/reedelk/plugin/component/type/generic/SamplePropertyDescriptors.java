package com.reedelk.plugin.component.type.generic;

import com.reedelk.plugin.component.domain.*;
import com.reedelk.plugin.fixture.ComponentNode2;

import static java.util.Arrays.asList;

public class SamplePropertyDescriptors {

    public static final TypeDescriptor stringTypeDescriptor = new TypePrimitiveDescriptor(String.class);
    public static final TypeDescriptor integerTypeDescriptor = new TypePrimitiveDescriptor(Integer.class);
    public static final TypeDescriptor booleanTypeDescriptor = new TypePrimitiveDescriptor(Boolean.class);
    public static final TypeDescriptor mapTypeDescriptor = new TypeMapDescriptor("properties");

    public static final ComponentPropertyDescriptor property1 =
            ComponentPropertyDescriptor.builder()
                    .propertyName("property1")
                    .type(stringTypeDescriptor)
                    .displayName("Property 1 Name")
                    .defaultValue("")
                    .build();

    public static final ComponentPropertyDescriptor property2 =
            ComponentPropertyDescriptor.builder()
                    .propertyName("property2")
                    .type(stringTypeDescriptor)
                    .displayName("Property 2 Name")
                    .defaultValue("")
                    .build();

    public static final ComponentPropertyDescriptor property3 =
            ComponentPropertyDescriptor.builder()
                    .propertyName("property3")
                    .type(stringTypeDescriptor)
                    .displayName("Property 3 Name")
                    .defaultValue("")
                    .build();

    public static final ComponentPropertyDescriptor property5 =
            ComponentPropertyDescriptor.builder()
                    .propertyName("property5")
                    .type(stringTypeDescriptor)
                    .displayName("Property 5 Name")
                    .defaultValue("")
                    .build();

    public static final ComponentPropertyDescriptor property6 =
            ComponentPropertyDescriptor.builder()
                    .propertyName("property6")
                    .type(integerTypeDescriptor)
                    .displayName("Property 6 Name")
                    .defaultValue("0")
                    .build();

    public static final TypeObjectDescriptor componentNode2TypeDescriptor =
            new TypeObjectDescriptor(ComponentNode2.class.getName(), asList(property5, property6), Shared.NO);

    public static final TypeObjectDescriptor componentNode2ShareableTypeDescriptor =
            new TypeObjectDescriptor(ComponentNode2.class.getName(), asList(property5, property6), Shared.YES);

    public static final ComponentPropertyDescriptor property4 =
            ComponentPropertyDescriptor.builder()
                    .propertyName("property4")
                    .type(componentNode2TypeDescriptor)
                    .displayName("Property 4 Name")
                    .defaultValue(null)
                    .build();

    public static final ComponentPropertyDescriptor property7 =
            ComponentPropertyDescriptor.builder()
                    .propertyName("property7")
                    .type(componentNode2ShareableTypeDescriptor)
                    .displayName("Property 7 Name")
                    .build();

    public static final ComponentPropertyDescriptor property8 =
            ComponentPropertyDescriptor.builder()
                    .propertyName("property8")
                    .type(mapTypeDescriptor)
                    .displayName("Property 8 Name")
                    .build();
}
