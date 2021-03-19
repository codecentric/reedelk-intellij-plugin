package de.codecentric.reedelk.plugin.component.type.unknown;

import de.codecentric.reedelk.module.descriptor.model.component.ComponentDescriptor;
import de.codecentric.reedelk.module.descriptor.model.component.ComponentType;
import de.codecentric.reedelk.plugin.commons.Images;
import de.codecentric.reedelk.plugin.commons.PredefinedPropertyDescriptor;
import de.codecentric.reedelk.runtime.api.component.Component;

import static de.codecentric.reedelk.plugin.message.ReedelkBundle.message;
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
