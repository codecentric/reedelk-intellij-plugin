package com.esb.plugin.component.type.generic;

import com.esb.plugin.component.domain.ComponentPropertyDescriptor;
import com.esb.plugin.component.domain.TypeDescriptor;
import com.esb.plugin.component.domain.TypeObjectDescriptor;
import com.esb.plugin.component.domain.TypePrimitiveDescriptor;
import com.esb.plugin.fixture.ComponentNode2;

import static com.esb.plugin.component.domain.ComponentPropertyDescriptor.PropertyRequired.REQUIRED;
import static java.util.Arrays.asList;

class SamplePropertyDescriptors {

    private static final TypeDescriptor stringTypeDescriptor = new TypePrimitiveDescriptor(String.class);
    private static final TypeDescriptor integerTypeDescriptor = new TypePrimitiveDescriptor(Integer.class);

    static final ComponentPropertyDescriptor property1 =
            ComponentPropertyDescriptor.builder()
                    .propertyName("property1")
                    .type(stringTypeDescriptor)
                    .displayName("Property 1 Name")
                    .defaultValue("")
                    .required(REQUIRED)
                    .build();

    static final ComponentPropertyDescriptor property2 =
            ComponentPropertyDescriptor.builder()
                    .propertyName("property2")
                    .type(stringTypeDescriptor)
                    .displayName("Property 2 Name")
                    .defaultValue("")
                    .required(REQUIRED)
                    .build();

    static final ComponentPropertyDescriptor property3 =
            ComponentPropertyDescriptor.builder()
                    .propertyName("property3")
                    .type(stringTypeDescriptor)
                    .displayName("Property 3 Name")
                    .defaultValue("")
                    .required(REQUIRED)
                    .build();

    static final ComponentPropertyDescriptor property5 =
            ComponentPropertyDescriptor.builder()
                    .propertyName("property5")
                    .type(stringTypeDescriptor)
                    .displayName("Property 5 Name")
                    .defaultValue("")
                    .required(REQUIRED)
                    .build();

    static final ComponentPropertyDescriptor property6 =
            ComponentPropertyDescriptor.builder()
                    .propertyName("property6")
                    .type(integerTypeDescriptor)
                    .displayName("Property 6 Name")
                    .defaultValue(0)
                    .required(REQUIRED)
                    .build();

    static final TypeObjectDescriptor componentNode2TypeDescriptor = new TypeObjectDescriptor(ComponentNode2.class.getName(), false, asList(property5, property6));

    static final TypeObjectDescriptor componentNode2ShareableTypeDescriptor = new TypeObjectDescriptor(ComponentNode2.class.getName(), true, asList(property5, property6));

    static final ComponentPropertyDescriptor property4 =
            ComponentPropertyDescriptor.builder()
                    .propertyName("property4")
                    .type(componentNode2TypeDescriptor)
                    .displayName("Property 4 Name")
                    .defaultValue(null)
                    .required(REQUIRED)
                    .build();

    static final ComponentPropertyDescriptor property7 =
            ComponentPropertyDescriptor.builder()
                    .propertyName("property7")
                    .type(componentNode2ShareableTypeDescriptor)
                    .displayName("Property 7 Name")
                    .required(REQUIRED)
                    .build();
}
