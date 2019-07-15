package com.esb.plugin.editor.properties.renderer;

import com.esb.plugin.component.domain.ComponentPropertyDescriptor;
import com.esb.plugin.editor.properties.accessor.PropertyAccessor;
import com.esb.plugin.editor.properties.widget.PropertyPanelContext;
import com.esb.plugin.editor.properties.widget.input.InputField;
import com.esb.plugin.editor.properties.widget.input.StringInputField;
import com.intellij.openapi.module.Module;

import javax.swing.*;

public class StringPropertyRenderer implements TypePropertyRenderer {
    @Override
    public JComponent render(Module module, ComponentPropertyDescriptor propertyDescriptor, PropertyAccessor propertyAccessor, PropertyPanelContext propertyPanelContext) {
        InputField<String> field = new StringInputField();
        field.setValue(propertyAccessor.get());
        field.addListener(propertyAccessor::set);
        return field;
    }
}
