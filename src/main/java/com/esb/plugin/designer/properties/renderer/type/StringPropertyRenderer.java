package com.esb.plugin.designer.properties.renderer.type;

import com.esb.plugin.component.ComponentData;
import com.esb.plugin.component.ComponentPropertyDescriptor;
import com.esb.plugin.component.TypeDescriptor;
import com.esb.plugin.converter.ValueConverter;
import com.esb.plugin.converter.ValueConverterFactory;
import com.esb.plugin.designer.properties.widget.StringInputField;
import com.esb.plugin.graph.GraphSnapshot;

import javax.swing.*;

public class StringPropertyRenderer implements TypePropertyRenderer {

    @Override
    public JComponent render(ComponentPropertyDescriptor descriptor, ComponentData componentData, GraphSnapshot snapshot) {
        String propertyName = descriptor.getPropertyName();

        TypeDescriptor propertyType = descriptor.getPropertyType();
        ValueConverter<?> converter = ValueConverterFactory.forType(propertyType);

        Object propertyValue = componentData.get(propertyName);
        String propertyValueAsString = converter.toString(propertyValue);


        StringInputField input = new StringInputField();
        input.setText(propertyValueAsString);
        input.addListener(valueAsString -> {
            componentData.set(propertyName, valueAsString);
            snapshot.onDataChange();
        });

        return input;
    }

}
