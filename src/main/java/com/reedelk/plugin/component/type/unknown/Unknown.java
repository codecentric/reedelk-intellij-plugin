package com.reedelk.plugin.component.type.unknown;

import com.reedelk.module.descriptor.model.component.ComponentDescriptor;
import com.reedelk.module.descriptor.model.component.ComponentType;
import com.reedelk.plugin.commons.Images;
import com.reedelk.plugin.commons.PredefinedPropertyDescriptor;
import com.reedelk.runtime.api.component.Component;

import static com.reedelk.plugin.message.ReedelkBundle.message;
import static java.util.Collections.singletonList;

public class Unknown implements Component {

    private Unknown() {
    }

    public static final ComponentDescriptor DESCRIPTOR;
    static {
        DESCRIPTOR = ComponentDescriptor.create()
                .properties(singletonList(PredefinedPropertyDescriptor.UNKNOWN_IMPLEMENTOR))
                .fullyQualifiedName(Unknown.class.getName())
                .displayName(message("component.unknown"))
                .type(ComponentType.UNKNOWN)
                .build();
        DESCRIPTOR.setImage(Images.Component.UknownComponent);
    }
}
