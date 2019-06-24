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
            new ComponentPropertyDescriptor("property1", stringTypeDescriptor, "Property 1 Name", "", REQUIRED);

    static final ComponentPropertyDescriptor property2 =
            new ComponentPropertyDescriptor("property2", stringTypeDescriptor, "Property 2 Name", "", REQUIRED);

    static final ComponentPropertyDescriptor property3 =
            new ComponentPropertyDescriptor("property3", stringTypeDescriptor, "Property 3 Name", "", REQUIRED);

    static final ComponentPropertyDescriptor property5 =
            new ComponentPropertyDescriptor("property5", stringTypeDescriptor, "Property 5 Name", "", REQUIRED);

    static final ComponentPropertyDescriptor property6 =
            new ComponentPropertyDescriptor("property6", integerTypeDescriptor, "Property 6 Name", 0, REQUIRED);

    static final TypeObjectDescriptor componentNode2TypeDescriptor = new TypeObjectDescriptor(ComponentNode2.class.getName(), false, asList(property5, property6));

    static final TypeObjectDescriptor componentNode2ShareableTypeDescriptor = new TypeObjectDescriptor(ComponentNode2.class.getName(), true, asList(property5, property6));

    static final ComponentPropertyDescriptor property4 =
            new ComponentPropertyDescriptor("property4", componentNode2TypeDescriptor, "Property 4 Name", null, REQUIRED);

    static final ComponentPropertyDescriptor property7 =
            new ComponentPropertyDescriptor("property7", componentNode2ShareableTypeDescriptor, "Property 7 Name", "", REQUIRED);

}
