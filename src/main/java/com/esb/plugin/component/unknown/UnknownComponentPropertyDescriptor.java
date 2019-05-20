package com.esb.plugin.component.unknown;

import com.esb.plugin.component.ComponentPropertyDescriptor;

public class UnknownComponentPropertyDescriptor extends ComponentPropertyDescriptor {

    public UnknownComponentPropertyDescriptor(String propertyName) {
        super(propertyName, new UnknownPropertyType(), null, null, false);
    }
}
