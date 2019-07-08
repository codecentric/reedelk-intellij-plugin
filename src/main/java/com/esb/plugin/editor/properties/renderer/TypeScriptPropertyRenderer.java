package com.esb.plugin.editor.properties.renderer;

import com.esb.plugin.component.domain.ComponentPropertyDescriptor;
import com.esb.plugin.editor.properties.accessor.PropertyAccessor;
import com.esb.plugin.editor.properties.widget.ContainerFactory;
import com.esb.plugin.editor.properties.widget.FormBuilder;
import com.esb.plugin.editor.properties.widget.input.script.ScriptInputField;
import com.intellij.openapi.module.Module;
import com.intellij.ui.components.JBPanel;

import javax.swing.*;

public class TypeScriptPropertyRenderer implements TypePropertyRenderer {

    @Override
    public JComponent render(Module module, ComponentPropertyDescriptor propertyDescriptor, PropertyAccessor propertyAccessor) {
        ScriptInputField field = new ScriptInputField(module);
        field.setValue(propertyAccessor.get());
        field.addListener(propertyAccessor::set);
        return field;
    }

    @Override
    public void addToParent(JComponent parent, JComponent rendered, String label) {
        // If the property type is a complex object, we wrap it in a
        // bordered box with title the name of the object property.
        JBPanel wrappedRenderedComponent = ContainerFactory
                .createObjectTypeContainer(label, rendered);
        FormBuilder.get()
                .addLastField(wrappedRenderedComponent, parent);
    }
}
