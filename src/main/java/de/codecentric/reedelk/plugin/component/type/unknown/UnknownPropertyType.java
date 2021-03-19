package de.codecentric.reedelk.plugin.component.type.unknown;

import de.codecentric.reedelk.module.descriptor.model.property.PropertyTypeDescriptor;

public class UnknownPropertyType implements PropertyTypeDescriptor {

    @Override
    public Class<?> getType() {
        return UnknownType.class;
    }

    public interface UnknownType {
    }
}
