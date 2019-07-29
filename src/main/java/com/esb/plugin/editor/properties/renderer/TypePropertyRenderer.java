package com.esb.plugin.editor.properties.renderer;

import com.esb.plugin.component.domain.ComponentPropertyDescriptor;
import com.esb.plugin.editor.properties.accessor.PropertyAccessor;
import com.esb.plugin.editor.properties.widget.FormBuilder;
import com.esb.plugin.editor.properties.widget.input.script.PropertyPanelContext;
import com.intellij.openapi.module.Module;

import javax.swing.*;

public interface TypePropertyRenderer {

    JComponent render(Module module, ComponentPropertyDescriptor propertyDescriptor, PropertyAccessor propertyAccessor, PropertyPanelContext tracker);

    default void addToParent(JComponent parent, JComponent rendered, String label) {
        FormBuilder.get()
                .addLabel(label, parent)
                .addLastField(rendered, parent);
    }
}
