package com.reedelk.plugin.component.type.unknown;

import com.reedelk.module.descriptor.model.ComponentDescriptor;
import com.reedelk.module.descriptor.model.ComponentType;
import com.reedelk.module.descriptor.model.PropertyDescriptor;
import com.reedelk.plugin.commons.Images;
import com.reedelk.plugin.commons.TypePrimitiveDescriptors;
import com.reedelk.runtime.api.component.Component;

import java.util.Collections;

public class Unknown implements Component {

    // TODO: Fix display name and so on.
    public static final ComponentDescriptor DESCRIPTOR;
    static {
        // Must add the icon
        DESCRIPTOR = ComponentDescriptor.create()
                .displayName("Unknown component")
                .fullyQualifiedName(Unknown.class.getName())
                .description("Unknown component")
                .propertyDescriptors(Collections.singletonList(PropertyDescriptor.builder()
                        .displayName("Unknown component")
                        .name("implementor")
                        .type(TypePrimitiveDescriptors.STRING)
                        .build()))
                .type(ComponentType.UNKNOWN)
                .build();

        DESCRIPTOR.setImage(Images.Component.UknownComponent);
    }
}
