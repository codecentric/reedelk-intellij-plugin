package com.esb.plugin.designer.properties.renderer;

import com.esb.plugin.component.ComponentData;
import com.esb.plugin.component.ComponentPropertyDescriptor;
import com.esb.plugin.component.EnumTypeDescriptor;
import com.esb.plugin.converter.PropertyValueConverter;
import com.esb.plugin.converter.PropertyValueConverterFactory;
import com.esb.plugin.designer.properties.widget.DefaultPropertiesPanel;
import com.esb.plugin.designer.properties.widget.FormBuilder;
import com.esb.plugin.designer.properties.widget.PropertyDropDown;
import com.esb.plugin.graph.GraphSnapshot;

public class EnumTypePropertyRenderer implements PropertyRenderer {

    @Override
    public void render(ComponentPropertyDescriptor descriptor, ComponentData componentData, DefaultPropertiesPanel parent, GraphSnapshot snapshot) {
        // This should be rendered as a dropdown.
        String propertyName = descriptor.getPropertyName();
        String displayName = descriptor.getDisplayName();

        EnumTypeDescriptor propertyType = (EnumTypeDescriptor) descriptor.getPropertyType();
        PropertyValueConverter<?> converter = PropertyValueConverterFactory.forType(propertyType);

        Object propertyValue = componentData.get(propertyName);
        String propertyValueAsString = converter.to(propertyValue);

        PropertyDropDown dropDown = new PropertyDropDown(propertyType.possibleValues());
        FormBuilder.get()
                .addLabel(displayName, parent)
                .addLastField(dropDown, parent);
    }
}
