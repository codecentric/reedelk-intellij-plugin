package com.esb.plugin.designer.properties.renderer.type;

import com.esb.plugin.component.ComponentData;
import com.esb.plugin.component.ComponentPropertyDescriptor;
import com.esb.plugin.component.EnumTypeDescriptor;
import com.esb.plugin.converter.ValueConverter;
import com.esb.plugin.converter.ValueConverterFactory;
import com.esb.plugin.designer.properties.widget.DefaultPropertiesPanel;
import com.esb.plugin.designer.properties.widget.FormBuilder;
import com.esb.plugin.designer.properties.widget.PropertyDropDown;
import com.esb.plugin.graph.GraphSnapshot;

import javax.swing.*;
import java.awt.*;

public class EnumRenderer implements TypeRenderer {

    @Override
    public void render(ComponentPropertyDescriptor descriptor, ComponentData componentData, DefaultPropertiesPanel parent, GraphSnapshot snapshot) {
        String propertyName = descriptor.getPropertyName();
        String displayName = descriptor.getDisplayName();

        EnumTypeDescriptor propertyType = (EnumTypeDescriptor) descriptor.getPropertyType();
        ValueConverter<?> converter = ValueConverterFactory.forType(propertyType);

        Object propertyValue = componentData.getOrDefault(propertyName, descriptor.getDefaultValue());
        Object propertyValueAsString = converter.to(propertyValue);

        PropertyDropDown dropDown = new PropertyDropDown(propertyType.possibleValues());
        dropDown.addListener(valueAsString -> {
            componentData.set(propertyName, valueAsString);
            snapshot.onDataChange();
        });
        dropDown.setSelectedItem(propertyValueAsString);

        JPanel dropDownContainer = new JPanel();
        dropDownContainer.setLayout(new BorderLayout());
        dropDownContainer.add(dropDown, BorderLayout.WEST);
        dropDownContainer.add(Box.createHorizontalGlue(), BorderLayout.CENTER);

        FormBuilder.get()
                .addLabel(displayName + ":", parent)
                .addLastField(dropDownContainer, parent);
    }
}
