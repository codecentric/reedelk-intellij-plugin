package com.esb.plugin.designer.properties.renderer;

import com.esb.plugin.component.ComponentData;
import com.esb.plugin.component.ComponentPropertyDescriptor;
import com.esb.plugin.component.PropertyTypeDescriptor;
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

        PropertyTypeDescriptor propertyType = descriptor.getPropertyType();
        PropertyValueConverter<?> converter = PropertyValueConverterFactory.forType(propertyType);

        Object propertyValue = componentData.get(propertyName);
        String propertyValueAsString = converter.to(propertyValue);

        PropertyInput input = new PropertyInput();
        input.setText(propertyValueAsString);
        input.addListener(valueAsString -> {
            Object valueAsTypedObject = converter.from(valueAsString);
            componentData.set(propertyName, valueAsTypedObject);
            snapshot.onDataChange();
        });

        FormBuilder.get()
                .addLabel(displayName, parent)
                .addLastField(input, parent);
    }

}
