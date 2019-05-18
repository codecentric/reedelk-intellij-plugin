package com.esb.plugin.designer.properties.renderer.type;

import com.esb.plugin.component.ComponentData;
import com.esb.plugin.component.ComponentPropertyDescriptor;
import com.esb.plugin.designer.properties.widget.input.InputField;
import com.esb.plugin.designer.properties.widget.input.StringInputField;
import com.esb.plugin.graph.GraphSnapshot;

import javax.swing.*;

public class StringPropertyRenderer implements TypePropertyRenderer {

    @Override
    public JComponent render(ComponentPropertyDescriptor descriptor, ComponentData componentData, GraphSnapshot snapshot) {
        String propertyName = descriptor.getPropertyName();
        Object propertyValue = componentData.get(propertyName);

        InputField<String> field = new StringInputField();
        field.setValue(propertyValue);
        field.addListener(value -> {
            componentData.set(propertyName, value);
            snapshot.onDataChange();
        });

        return field;
    }
}
