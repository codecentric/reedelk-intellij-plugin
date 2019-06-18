package com.esb.plugin.editor.properties.renderer.type;

import com.esb.plugin.component.domain.ComponentDataHolder;
import com.esb.plugin.component.domain.ComponentPropertyDescriptor;
import com.esb.plugin.component.domain.TypeDescriptor;
import com.esb.plugin.component.domain.TypeObjectDescriptor;
import com.esb.plugin.editor.properties.accessor.PropertyAccessor;
import com.esb.plugin.editor.properties.accessor.PropertyAccessorFactory;
import com.esb.plugin.editor.properties.widget.DefaultPropertiesPanel;
import com.esb.plugin.editor.properties.widget.FormBuilder;
import com.esb.plugin.graph.FlowSnapshot;

import javax.swing.*;
import java.util.List;

public class TypeObjectPropertyRenderer implements TypePropertyRenderer {

    @Override
    public JComponent render(ComponentPropertyDescriptor descriptor, PropertyAccessor accessor, FlowSnapshot snapshot) {
        TypeObjectDescriptor objectDescriptor = (TypeObjectDescriptor) descriptor.getPropertyType();
        List<ComponentPropertyDescriptor> objectProperties = objectDescriptor.getObjectProperties();

        DefaultPropertiesPanel propertiesPanel = new DefaultPropertiesPanel();
        objectProperties.forEach(desc -> {

            final String displayName = desc.getDisplayName();
            final TypeDescriptor propertyType = desc.getPropertyType();

            // The accessor of type object returns a TypeObject map.
            ComponentDataHolder dataHolder = (ComponentDataHolder) accessor.get();

            PropertyAccessor acc = PropertyAccessorFactory.get()
                    .typeDescriptor(desc.getPropertyType())
                    .dataHolder(dataHolder)
                    .propertyName(desc.getPropertyName())
                    .build();

            TypeRendererFactory typeRendererFactory = TypeRendererFactory.get();
            JComponent renderedComponent = typeRendererFactory.from(propertyType)
                    .render(desc, acc, snapshot);

            FormBuilder.get()
                    .addLabel(displayName, propertiesPanel)
                    .addLastField(renderedComponent, propertiesPanel);
        });
        return propertiesPanel;
    }
}
