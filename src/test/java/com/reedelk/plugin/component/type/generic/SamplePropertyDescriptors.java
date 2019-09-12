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
                    .type(stringTypeDescriptor)
                    .propertyName("property1")
                    .displayName("Property 1 Name")
                    .defaultValue("")
                    .build();

    public static final ComponentPropertyDescriptor property2 =
            ComponentPropertyDescriptor.builder()
                    .type(stringTypeDescriptor)
                    .propertyName("property2")
                    .displayName("Property 2 Name")
                    .defaultValue("")
                    .build();

    public static final ComponentPropertyDescriptor property3 =
            ComponentPropertyDescriptor.builder()
                    .type(stringTypeDescriptor)
                    .propertyName("property3")
                    .displayName("Property 3 Name")
                    .defaultValue("")
                    .build();

    public static final ComponentPropertyDescriptor property5 =
            ComponentPropertyDescriptor.builder()
                    .type(stringTypeDescriptor)
                    .propertyName("property5")
                    .displayName("Property 5 Name")
                    .defaultValue("")
                    .build();

    public static final ComponentPropertyDescriptor property6 =
            ComponentPropertyDescriptor.builder()
                    .type(integerTypeDescriptor)
                    .propertyName("property6")
                    .displayName("Property 6 Name")
                    .defaultValue("0")
                    .build();

    public static final TypeObjectDescriptor componentNode2TypeDescriptor =
            new TypeObjectDescriptor(ComponentNode2.class.getName(), asList(property5, property6), Shared.NO, Collapsible.NO);

    public static final TypeObjectDescriptor componentNode2ShareableTypeDescriptor =
            new TypeObjectDescriptor(ComponentNode2.class.getName(), asList(property5, property6), Shared.YES, Collapsible.NO);

    public static final ComponentPropertyDescriptor property4 =
            ComponentPropertyDescriptor.builder()
                    .type(componentNode2TypeDescriptor)
                    .propertyName("property4")
                    .displayName("Property 4 Name")
                    .defaultValue(null)
                    .build();

    public static final ComponentPropertyDescriptor property7 =
            ComponentPropertyDescriptor.builder()
                    .type(componentNode2ShareableTypeDescriptor)
                    .propertyName("property7")
                    .displayName("Property 7 Name")
                    .build();

    public static final ComponentPropertyDescriptor property8 =
            ComponentPropertyDescriptor.builder()
                    .type(mapTypeDescriptor)
                    .propertyName("property8")
                    .displayName("Property 8 Name")
                    .build();
}
