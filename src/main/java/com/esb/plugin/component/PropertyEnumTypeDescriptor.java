package com.esb.plugin.component;

import com.esb.plugin.designer.properties.PropertyRendererVisitable;
import com.esb.plugin.designer.properties.PropertyRendererVisitor;

public class PropertyEnumTypeDescriptor implements PropertyRendererVisitable {

    @Override
    public void accept(PropertyRendererVisitor visitor) {
        visitor.visit(this);
    }
}
