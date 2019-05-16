package com.esb.plugin.designer.properties.renderer;

import com.esb.plugin.component.ComponentData;
import com.esb.plugin.component.ComponentPropertyDescriptor;
import com.esb.plugin.component.PropertyTypeDescriptor;
import com.esb.plugin.converter.PropertyValueConverter;
import com.esb.plugin.converter.PropertyValueConverterFactory;
import com.esb.plugin.designer.properties.widget.DefaultPropertiesPanel;
import com.esb.plugin.designer.properties.widget.FormBuilder;
import com.esb.plugin.designer.properties.widget.IntegerInputField;
import com.esb.plugin.designer.properties.widget.PropertyInput;
import com.esb.plugin.graph.GraphSnapshot;

import javax.swing.*;
import java.awt.*;

public class PrimitiveTypePropertyRenderer implements PropertyRenderer {

    @Override
    public void render(ComponentPropertyDescriptor descriptor, ComponentData componentData, DefaultPropertiesPanel parent, GraphSnapshot snapshot) {
        String propertyName = descriptor.getPropertyName();
        String displayName = descriptor.getDisplayName();


        JComponent input = createInputComponent(propertyName, componentData, snapshot, descriptor);

        FormBuilder.get()
                .addLabel(displayName, parent)
                .addLastField(input, parent);
    }

    private JComponent createInputComponent(String propertyName,
                                            ComponentData componentData,
                                            GraphSnapshot snapshot,
                                            ComponentPropertyDescriptor descriptor) {

        PropertyTypeDescriptor propertyType = descriptor.getPropertyType();
        PropertyValueConverter<?> converter = PropertyValueConverterFactory.forType(propertyType);


        Object propertyValue = componentData.get(propertyName);
        String propertyValueAsString = converter.to(propertyValue);

        if (propertyType.type().equals(long.class) || propertyType.type().equals(Long.class)) {
            JPanel panel = new JPanel();
            panel.setLayout(new BorderLayout());
            IntegerInputField integerInputField = new IntegerInputField();
            integerInputField.setColumns(10);
            integerInputField.setValue(converter.from(propertyValueAsString));
            integerInputField.addListener(value -> {
                componentData.set(propertyName, value);
                snapshot.onDataChange();
            });
            panel.add(integerInputField, BorderLayout.WEST);
            panel.add(Box.createHorizontalBox(), BorderLayout.CENTER);
            return panel;

        } else {
            PropertyInput input = new PropertyInput();
            input.setText(propertyValueAsString);
            input.addListener(valueAsString -> {
                componentData.set(propertyName, valueAsString);
                snapshot.onDataChange();
            });
            return input;
        }
    }

}
