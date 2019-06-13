package com.esb.plugin.editor.properties.renderer.type;

import com.esb.plugin.component.domain.ComponentData;
import com.esb.plugin.component.domain.ComponentPropertyDescriptor;
import com.esb.plugin.component.domain.TypeEnumDescriptor;
import com.esb.plugin.editor.properties.widget.input.EnumDropDown;
import com.esb.plugin.graph.FlowSnapshot;

import javax.swing.*;
import java.awt.*;

public class EnumPropertyRenderer implements TypePropertyRenderer {

    @Override
    public JComponent render(ComponentPropertyDescriptor descriptor, ComponentData componentData, FlowSnapshot snapshot) {
        String propertyName = descriptor.getPropertyName();

        TypeEnumDescriptor propertyType = (TypeEnumDescriptor) descriptor.getPropertyType();
        Object propertyValue = componentData.get(propertyName);

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
