package com.esb.plugin.designer.properties.renderer.type;

import com.esb.plugin.component.ComponentData;
import com.esb.plugin.component.ComponentPropertyDescriptor;
import com.esb.plugin.designer.properties.widget.input.BooleanCheckbox;
import com.esb.plugin.graph.GraphSnapshot;

import javax.swing.*;
import java.awt.*;

import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.WEST;

public class BooleanPropertyRenderer implements TypePropertyRenderer {

    @Override
    public JComponent render(ComponentPropertyDescriptor descriptor, ComponentData componentData, GraphSnapshot snapshot) {
        String propertyName = descriptor.getPropertyName();
        Object propertyValue = componentData.getOrDefault(propertyName, descriptor.getDefaultValue());

        BooleanCheckbox checkbox = new BooleanCheckbox();
        checkbox.addListener(valueAsString -> {
            componentData.set(propertyName, valueAsString);
            snapshot.onDataChange();
        });
        checkbox.setValue(propertyValue);

        JPanel container = new JPanel();
        container.setLayout(new BorderLayout());
        container.add(checkbox, WEST);
        container.add(Box.createHorizontalGlue(), CENTER);
        return container;
    }
}
