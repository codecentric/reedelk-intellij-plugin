package com.esb.plugin.component;

import com.esb.plugin.designer.properties.PropertyRendererVisitable;
import com.esb.plugin.designer.properties.PropertyRendererVisitor;

public class PropertyPrimitiveTypeDescriptor implements PropertyRendererVisitable {

    private final Class<?> propertyType;

    public PropertyPrimitiveTypeDescriptor(Class<?> propertyType) {
        this.propertyType = null;
    }

    @Override
    public void accept(PropertyRendererVisitor visitor) {
        visitor.visit(this);
    }
}
