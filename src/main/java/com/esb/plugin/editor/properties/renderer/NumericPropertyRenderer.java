package com.esb.plugin.editor.properties.renderer;

import com.esb.plugin.component.domain.ComponentPropertyDescriptor;
import com.esb.plugin.editor.properties.accessor.PropertyAccessor;
import com.esb.plugin.editor.properties.widget.DisposablePanel;
import com.esb.plugin.editor.properties.widget.input.InputField;
import com.esb.plugin.editor.properties.widget.input.script.PropertyPanelContext;
import com.intellij.openapi.module.Module;

import javax.swing.*;
import java.awt.*;

import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.WEST;
import static javax.swing.Box.createHorizontalBox;

public abstract class NumericPropertyRenderer<T> implements TypePropertyRenderer {

    @Override
    public JComponent render(Module module, ComponentPropertyDescriptor propertyDescriptor, PropertyAccessor propertyAccessor, PropertyPanelContext propertyPanelContext) {
        InputField<T> inputField = getInputField();
        inputField.setValue(propertyAccessor.get());
        inputField.addListener(propertyAccessor::set);

        JPanel inputFieldContainer = new DisposablePanel(new BorderLayout());
        inputFieldContainer.add(inputField, WEST);
        inputFieldContainer.add(createHorizontalBox(), CENTER);
        return inputFieldContainer;
    }

    protected abstract InputField<T> getInputField();
}
