package com.esb.plugin.designer.properties.renderer.type;

import com.esb.plugin.component.ComponentData;
import com.esb.plugin.component.ComponentPropertyDescriptor;
import com.esb.plugin.designer.properties.widget.NumericInputField;
import com.esb.plugin.graph.GraphSnapshot;

import javax.swing.*;

abstract class AbstractPropertyRenderer<T> implements TypePropertyRenderer {

    @Override
    public JComponent render(ComponentPropertyDescriptor descriptor, ComponentData componentData, GraphSnapshot snapshot) {
        String propertyName = descriptor.getPropertyName();
        Object propertyValue = componentData.get(propertyName);

        NumericInputField<T> field = getInputField();
        field.setValue(propertyValue);
        field.addListener(value -> {
            componentData.set(propertyName, value);
            snapshot.onDataChange();
        });

        return field;
    }

    protected abstract NumericInputField<T> getInputField();
}
