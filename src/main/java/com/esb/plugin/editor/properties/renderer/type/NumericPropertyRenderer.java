package com.esb.plugin.editor.properties.renderer.type;

import com.esb.plugin.component.domain.ComponentPropertyDescriptor;
import com.esb.plugin.editor.properties.accessor.PropertyAccessor;
import com.esb.plugin.editor.properties.widget.input.InputField;
import com.esb.plugin.graph.FlowSnapshot;

import javax.swing.*;
import java.awt.*;

import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.WEST;

public abstract class NumericPropertyRenderer<T> implements TypePropertyRenderer {

    protected abstract InputField<T> getInputField();

    @Override
    public JComponent render(ComponentPropertyDescriptor descriptor, PropertyAccessor accessor, FlowSnapshot snapshot) {
        InputField<T> inputField = getInputField();
        inputField.setValue(accessor.get());
        inputField.addListener(value -> {
            accessor.set(value);
            snapshot.onDataChange();
        });
        JPanel inputFieldContainer = new JPanel();
        inputFieldContainer.setLayout(new BorderLayout());
        inputFieldContainer.add(inputField, WEST);
        inputFieldContainer.add(Box.createHorizontalBox(), CENTER);
        return inputFieldContainer;
    }
}
