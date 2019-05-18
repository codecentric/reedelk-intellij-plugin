package com.esb.plugin.designer.properties.renderer.type;

import com.esb.plugin.component.ComponentData;
import com.esb.plugin.component.ComponentPropertyDescriptor;
import com.esb.plugin.designer.properties.widget.input.InputField;
import com.esb.plugin.graph.GraphSnapshot;

import javax.swing.*;
import java.awt.*;

import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.WEST;

public abstract class NumericPropertyRenderer<T> implements TypePropertyRenderer {

    @Override
    public JComponent render(ComponentPropertyDescriptor descriptor, ComponentData componentData, GraphSnapshot snapshot) {
        String propertyName = descriptor.getPropertyName();
        Object propertyValue = componentData.get(propertyName);

        InputField<T> inputField = getInputField();
        inputField.setValue(propertyValue);
        inputField.addListener(value -> {
            componentData.set(propertyName, value);
            snapshot.onDataChange();
        });

        JPanel inputFieldContainer = new JPanel();
        inputFieldContainer.setLayout(new BorderLayout());
        inputFieldContainer.add(inputField, WEST);
        inputFieldContainer.add(Box.createHorizontalBox(), CENTER);
        return inputFieldContainer;
    }

    protected abstract InputField<T> getInputField();
}