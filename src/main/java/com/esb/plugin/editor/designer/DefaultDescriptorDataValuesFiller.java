package com.esb.plugin.editor.designer;

import com.esb.plugin.component.domain.ComponentData;
import com.esb.plugin.component.domain.TypeDescriptor;
import com.esb.plugin.component.domain.TypeObjectDescriptor;
import com.esb.plugin.graph.node.GraphNode;

import static com.esb.internal.commons.JsonParser.Component;
import static com.esb.plugin.component.domain.TypeObjectDescriptor.TypeObject;

public class DefaultDescriptorDataValuesFiller {

    public static void fill(GraphNode node) {
        ComponentData componentData = node.componentData();
        componentData.getPropertiesDescriptors().forEach(descriptor -> {
            String propertyName = descriptor.getPropertyName();
            TypeDescriptor propertyType = descriptor.getPropertyType();
            if (propertyType instanceof TypeObjectDescriptor) {
                TypeObjectDescriptor typeObjectDescriptor = (TypeObjectDescriptor) propertyType;
                if (typeObjectDescriptor.isShareable()) {
                    TypeObject nested = typeObjectDescriptor.newInstance();
                    nested.set(Component.configRef(), TypeObject.DEFAULT_CONFIG_REF);
                    componentData.set(propertyName, nested);
                } else {
                    fillTypeObjectDescriptor(componentData, propertyName, typeObjectDescriptor);
                }
            } else {
                Object defaultValue = descriptor.getDefaultValue();
                componentData.set(propertyName, defaultValue);
            }
        });
    }

    private static void fillTypeObjectDescriptor(ComponentData componentData, String propertyName, TypeObjectDescriptor propertyType) {
        TypeObject typeObject = propertyType.newInstance();
        componentData.set(propertyName, typeObject);
        propertyType
                .getObjectProperties()
                .forEach(nestedDescriptor ->
                        typeObject.set(nestedDescriptor.getPropertyName(), nestedDescriptor.getDefaultValue()));
    }
}
