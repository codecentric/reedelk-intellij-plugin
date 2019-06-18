package com.esb.plugin.editor.properties.renderer.type;

import com.esb.plugin.component.domain.ComponentPropertyDescriptor;
import com.esb.plugin.component.domain.TypeEnumDescriptor;
import com.esb.plugin.editor.properties.accessor.PropertyAccessor;
import com.esb.plugin.editor.properties.widget.input.EnumDropDown;
import com.esb.plugin.graph.FlowSnapshot;

import javax.swing.*;
import java.awt.*;

public class EnumPropertyRenderer implements TypePropertyRenderer {

    @Override
    public JComponent render(ComponentPropertyDescriptor descriptor, PropertyAccessor accessor, FlowSnapshot snapshot) {
        TypeEnumDescriptor propertyType = (TypeEnumDescriptor) descriptor.getPropertyType();
        EnumDropDown dropDown = new EnumDropDown(propertyType.possibleValues());
        dropDown.setValue(accessor.get());
        dropDown.addListener(value -> {
            accessor.set(value);
            snapshot.onDataChange();
        });
        JPanel dropDownContainer = new JPanel();
        dropDownContainer.setLayout(new BorderLayout());
        dropDownContainer.add(dropDown, BorderLayout.WEST);
        dropDownContainer.add(Box.createHorizontalGlue(), BorderLayout.CENTER);
        return dropDownContainer;
    }
}
