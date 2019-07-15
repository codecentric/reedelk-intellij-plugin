package com.esb.plugin.editor.properties.renderer;

import com.esb.plugin.component.domain.ComponentPropertyDescriptor;
import com.esb.plugin.editor.properties.accessor.PropertyAccessor;
import com.esb.plugin.editor.properties.widget.PropertyPanelContext;
import com.intellij.openapi.module.Module;

import javax.swing.*;

public class UnknownPropertyRenderer implements TypePropertyRenderer {

    @Override
    public JComponent render(Module module, ComponentPropertyDescriptor propertyDescriptor, PropertyAccessor propertyAccessor, PropertyPanelContext propertyPanelContext) {
        throw new UnsupportedOperationException();
    }
}
