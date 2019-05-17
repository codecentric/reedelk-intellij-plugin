package com.esb.plugin.designer.properties.renderer.type;

import com.esb.plugin.component.ComponentData;
import com.esb.plugin.component.ComponentPropertyDescriptor;
import com.esb.plugin.designer.properties.widget.IntegerInputField;
import com.esb.plugin.graph.GraphSnapshot;

import javax.swing.*;

public class IntegerPropertyRenderer implements TypePropertyRenderer {

    @Override
    public JComponent render(ComponentPropertyDescriptor descriptor, ComponentData componentData, GraphSnapshot snapshot) {
        String propertyName = descriptor.getPropertyName();
        Object propertyValue = componentData.get(propertyName);

        IntegerInputField field = new IntegerInputField();
        field.setValue(propertyValue);
        field.addListener(value -> {
            componentData.set(propertyName, value);
            snapshot.onDataChange();
        });
        return field;
    }
}
