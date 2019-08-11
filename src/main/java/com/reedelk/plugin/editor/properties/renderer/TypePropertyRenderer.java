package com.reedelk.plugin.editor.properties.renderer;

import com.intellij.openapi.module.Module;
import com.reedelk.plugin.component.domain.ComponentPropertyDescriptor;
import com.reedelk.plugin.editor.properties.accessor.PropertyAccessor;
import com.reedelk.plugin.editor.properties.widget.FormBuilder;
import com.reedelk.plugin.editor.properties.widget.input.script.PropertyPanelContext;

import javax.swing.*;

public interface TypePropertyRenderer {

    JComponent render(Module module, ComponentPropertyDescriptor propertyDescriptor, PropertyAccessor propertyAccessor, PropertyPanelContext tracker);

    default void addToParent(JComponent parent, JComponent rendered, String label) {
        FormBuilder.get()
                .addLabel(label, parent)
                .addLastField(rendered, parent);
    }
}
