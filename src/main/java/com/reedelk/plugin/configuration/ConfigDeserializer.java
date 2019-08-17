package com.reedelk.plugin.configuration;

import com.reedelk.plugin.component.domain.ComponentDataHolder;
import com.reedelk.plugin.component.domain.ComponentPropertyDescriptor;
import com.reedelk.plugin.component.domain.TypeObjectDescriptor;
import com.reedelk.plugin.component.type.generic.GenericComponentDeserializer;
import org.json.JSONObject;

import static com.reedelk.runtime.commons.JsonParser.Config;

public class ConfigDeserializer {

    private ConfigDeserializer() {
    }

    public static ComponentDataHolder deserialize(String json, ComponentPropertyDescriptor componentPropertyDescriptor) {
        TypeObjectDescriptor propertyType = (TypeObjectDescriptor) componentPropertyDescriptor.getPropertyType();
        TypeObjectDescriptor.TypeObject typeObject = propertyType.newInstance();
        JSONObject configDefinition = new JSONObject(json);

        typeObject.set(Config.id(), configDefinition.getString(Config.id()));
        typeObject.set(Config.title(), configDefinition.getString(Config.title()));


        configDefinition.keys().forEachRemaining(propertyName -> {
            propertyType.getObjectProperties()
                    .stream()
                    .filter(componentPropertyDescriptor1 -> componentPropertyDescriptor1.getPropertyName().equals(propertyName))
                    .findFirst().ifPresent(componentPropertyDescriptor12 ->
                    GenericComponentDeserializer.deserialize(configDefinition, typeObject, componentPropertyDescriptor12));
        });


        return typeObject;
    }
}
