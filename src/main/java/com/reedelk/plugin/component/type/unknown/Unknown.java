package com.reedelk.plugin.component.type.unknown;

import com.reedelk.module.descriptor.model.ComponentDescriptor;
import com.reedelk.module.descriptor.model.ComponentType;
import com.reedelk.module.descriptor.model.PropertyDescriptor;
import com.reedelk.plugin.commons.Images;
import com.reedelk.plugin.commons.TypePrimitiveDescriptors;
import com.reedelk.runtime.api.component.Component;

import static com.reedelk.plugin.message.ReedelkBundle.message;
import static com.reedelk.runtime.commons.JsonParser.Implementor;
import static java.util.Collections.singletonList;

public class Unknown implements Component {

    private Unknown() {
    }

    public static final ComponentDescriptor DESCRIPTOR;
    static {
        DESCRIPTOR = ComponentDescriptor.create()
                .displayName(message("component.unknown"))
                .fullyQualifiedName(Unknown.class.getName())
                .propertyDescriptors(singletonList(PropertyDescriptor.builder()
                        .displayName(message("component.unknown.implementor"))
                        .name(Implementor.name())
                        .type(TypePrimitiveDescriptors.STRING)
                        .build()))
                .type(ComponentType.UNKNOWN)
                .build();
        DESCRIPTOR.setImage(Images.Component.UknownComponent);
    }
}
