package com.esb.plugin.designer.properties.renderer.type;

import com.esb.plugin.component.ComponentData;
import com.esb.plugin.component.ComponentPropertyDescriptor;
import com.esb.plugin.component.EnumTypeDescriptor;
import com.esb.plugin.designer.properties.widget.input.EnumDropDown;
import com.esb.plugin.graph.GraphSnapshot;

import javax.swing.*;
import java.awt.*;

public class EnumPropertyRenderer implements TypePropertyRenderer {

    @Override
    public JComponent render(ComponentPropertyDescriptor descriptor, ComponentData componentData, GraphSnapshot snapshot) {
        String propertyName = descriptor.getPropertyName();

        EnumTypeDescriptor propertyType = (EnumTypeDescriptor) descriptor.getPropertyType();
        Object propertyValue = componentData.getOrDefault(propertyName, descriptor.getDefaultValue());

        EnumDropDown dropDown = new EnumDropDown(propertyType.possibleValues());
        dropDown.setValue(propertyValue);
        dropDown.addListener(value -> {
            componentData.set(propertyName, value);
            snapshot.onDataChange();
        });

        JPanel dropDownContainer = new JPanel();
        dropDownContainer.setLayout(new BorderLayout());
        dropDownContainer.add(dropDown, BorderLayout.WEST);
        dropDownContainer.add(Box.createHorizontalGlue(), BorderLayout.CENTER);
        return dropDownContainer;
    }
}
