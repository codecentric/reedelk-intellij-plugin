package com.esb.plugin.designer.properties;

import com.esb.plugin.component.ComponentData;
import com.esb.plugin.component.ComponentPropertyDescriptor;
import com.esb.plugin.converter.PropertyValueConverter;
import com.esb.plugin.converter.PropertyValueConverterFactory;
import com.esb.plugin.designer.properties.widget.DefaultPropertiesPanel;
import com.esb.plugin.designer.properties.widget.FormBuilder;
import com.esb.plugin.designer.properties.widget.PropertyInput;
import com.esb.plugin.graph.GraphSnapshot;

public class PrimitiveTypePropertyRenderer implements PropertyRenderer {

    @Override
    public void render(ComponentPropertyDescriptor descriptor, ComponentData componentData, DefaultPropertiesPanel parent, GraphSnapshot snapshot) {
        String propertyName = descriptor.getPropertyName();
        String displayName = descriptor.getDisplayName();

        PropertyInput input = new PropertyInput();

        Class<?> propertyType = componentData.getPropertyType(propertyName);
        PropertyValueConverter<?> converter = PropertyValueConverterFactory.forType(propertyType);

        Object propertyValue = componentData.get(propertyName);
        String propertyValueAsString = converter.to(propertyValue);

        input.setText(propertyValueAsString);

        input.addListener(valueAsString -> {
            Object valueAsObject = converter.from(valueAsString);
            componentData.set(propertyName, valueAsObject);
            snapshot.onDataChange();
        });

        FormBuilder.get().addLabel(displayName, parent).addLastField(input, parent);

    }

}
